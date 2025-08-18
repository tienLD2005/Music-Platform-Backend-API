package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.services.IClientPaymentService;
import com.ra.base_spring_boot.services.paypal.PaypalService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;
    private final IClientPaymentService paymentService;

    @GetMapping("/success")
    public void success(
            @RequestParam("token") String token,
            @RequestParam("PayerID") String payerId,
            HttpServletResponse response
    ) throws IOException{
        SubscriptionResponseDTO dto = paymentService.processPaypalSuccess(token, payerId);
        response.sendRedirect("http://localhost:3000/");
    }


    @GetMapping("/cancel")
    public ResponseEntity<?> cancel() {
        return ResponseEntity.ok(Map.of(
                "message", "Payment cancelled"
        ));
    }
}
