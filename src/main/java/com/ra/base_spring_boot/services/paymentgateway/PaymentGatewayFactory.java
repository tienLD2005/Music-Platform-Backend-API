package com.ra.base_spring_boot.services.paymentgateway;

import com.ra.base_spring_boot.model.constants.PaymentMethod;
import com.ra.base_spring_boot.services.IPaymentGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final Map<String, IPaymentGatewayService> gateways;

    @PostConstruct
    public void validateGateways() {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (!gateways.containsKey(method.name())) {
                throw new IllegalStateException("No payment gateway implementation found for " + method);
            }
        }
    }

    public IPaymentGatewayService getGateway(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        
        IPaymentGatewayService gateway = gateways.get(method.name());
        if (gateway == null) {
            throw new IllegalArgumentException("No payment gateway implementation found for " + method);
        }
        return gateway;
    }
}
