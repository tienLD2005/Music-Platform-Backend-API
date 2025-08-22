package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.SubscriptionRequestDTO;
import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.model.constants.PaymentMethod;
import com.ra.base_spring_boot.services.IClientPaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments controller")
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

    @PostMapping("/create")
    public ResponseWrapper<String> createPayment(@Valid @RequestBody SubscriptionRequestDTO requestDTO) {
        String approvalUrl = paymentService.createPayment(requestDTO);
        return ResponseWrapper.<String>builder()
                .status(HttpStatus.CREATED)
                .code(HttpStatus.CREATED.value())
                .data(approvalUrl)
                .build();
    }
}

