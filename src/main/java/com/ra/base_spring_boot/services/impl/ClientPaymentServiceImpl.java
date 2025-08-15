package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;
import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.repository.IPaymentRepository;
import com.ra.base_spring_boot.repository.ISubscriptionRepository;
import com.ra.base_spring_boot.services.IClientPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientPaymentServiceImpl implements IClientPaymentService{

    private final IPaymentRepository paymentRepository;
    private final ISubscriptionRepository subscriptionRepository;
    
    @Override
    public PaymentResponseDTO getPaymentDetail(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToResponseDTO(payment);
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}