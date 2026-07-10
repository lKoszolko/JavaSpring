package org.example.web;

import org.example.services.impl.RentalHibernateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payments")
public class PaymentController {
    private final RentalHibernateService rentalService;

    public PaymentController(RentalHibernateService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam String rentalId) {
        rentalService.finalizeReturnAfterPayment(rentalId);

        return ResponseEntity.ok("Opłacono wynajem pomyślnie, pojazd został zwrócony");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity.badRequest().body("Anulowałeś płatność. Pojazd nadal jest przypisany do Twojego konta.");
    }
}
