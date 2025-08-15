package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;

public interface IClientPaymentService{
    PaymentResponseDTO getPaymentDetail(Long paymentId);
}
