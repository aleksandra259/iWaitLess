package com.iwaitless.application.persistence.repository.nomenclatures;


import com.iwaitless.application.persistence.entity.nomenclatures.PaymentWay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentWayRepository extends JpaRepository<PaymentWay, String> {

}
