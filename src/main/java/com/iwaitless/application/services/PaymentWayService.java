package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.nomenclatures.PaymentWay;
import com.iwaitless.application.persistence.repository.nomenclatures.PaymentWayRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentWayService {

    private final PaymentWayRepository paymentWayRepository;

    public PaymentWayService(PaymentWayRepository paymentWayRepository) {
        this.paymentWayRepository = paymentWayRepository;
    }

    public PaymentWay findStatusById(String id) {
        return paymentWayRepository.findById(id).orElse(null);
    }
}