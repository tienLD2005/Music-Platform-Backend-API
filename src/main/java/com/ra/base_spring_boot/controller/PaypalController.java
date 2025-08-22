package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.services.IClientPaymentService;
import com.ra.base_spring_boot.services.paypal.PaypalService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
@Hidden
public class PaypalController {
    private final PaypalService paypalService;
    private final IClientPaymentService paymentService;

    @GetMapping("/success")
    public ResponseEntity<ResponseWrapper<String>> success(
            @RequestParam("token") String token,
            @RequestParam("PayerID") String payerId
    ) {
        paymentService.processPaypalSuccess(token, payerId);

        ResponseWrapper<String> response = ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data("Payment success")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cancel")
    public ResponseEntity<ResponseWrapper<String>> cancel() {
        ResponseWrapper<String> response = ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data("Payment cancelled")
                .build();

        return ResponseEntity.ok(response);
    }

}
