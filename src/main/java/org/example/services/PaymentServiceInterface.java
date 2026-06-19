package org.example.services;

import com.stripe.exception.StripeException;
import org.example.models.Rental;

import java.time.LocalDateTime;

public interface PaymentServiceInterface {
    public long calculateTotalDays(LocalDateTime rentDate, LocalDateTime returnDate);
    public double calculateTotalAmount(Rental rental);
    public String createStripeCheckoutSession(Rental rental) throws StripeException;
}
