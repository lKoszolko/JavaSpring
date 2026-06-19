package org.example.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.example.models.Rental;
import org.example.services.PaymentServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PaymentService implements PaymentServiceInterface {

    public PaymentService(@Value("${stripe.api.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public long calculateTotalDays(LocalDateTime rentDate, LocalDateTime returnDate) {
        if (returnDate.isBefore(rentDate)) {
            throw new IllegalArgumentException("Data zwrotu nie może być wcześniejsza niż data wypożyczenia!");
        }

        Duration duration = Duration.between(rentDate, returnDate);
        long minutes = duration.toMinutes();

        return (long) Math.ceil((double) minutes / 1440.0);
    }

    public double calculateTotalAmount(Rental rental) {
        double pricePerDay = rental.getVehicle().getPrice();

        LocalDateTime start = rental.getRentDateTime();
        LocalDateTime end = rental.getReturnDateTime();

        if (end == null) {
            throw new IllegalStateException("Nie można obliczyć kosztu dla pojazdu, który nie został jeszcze zwrócony!");
        }

        long totalDays = calculateTotalDays(start, end);

        if (totalDays == 0) {
            totalDays = 1;
        }

        return pricePerDay * totalDays;
    }

    public String createStripeCheckoutSession(Rental rental) throws StripeException {
        double totalAmount = calculateTotalAmount(rental);
        long amountInCents = (long) (totalAmount * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5000/api/payments/success?rentalId=" + rental.getId())
                .setCancelUrl("http://localhost:5000/api/payments/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("pln")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Wypożyczenie: " + rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel())
                                                                .setDescription("Opłata skalkulowana przez system (JPA LocalDateTime)")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}