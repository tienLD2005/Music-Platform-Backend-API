package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;
import com.ra.base_spring_boot.services.IClientPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class ClientPaymentController {

    private final IClientPaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseWrapper<PaymentResponseDTO> getPaymentDetail(@PathVariable Long paymentId) {
        return ResponseWrapper.<PaymentResponseDTO>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(paymentService.getPaymentDetail(paymentId))
                .build();
    }


}
