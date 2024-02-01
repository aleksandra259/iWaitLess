package com.iwaitless.application.persistence.entity.nomenclatures;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Table(name = "HT_PAYMENT_WAY")
@Entity
@AllArgsConstructor
public class PaymentWay extends AbstractNomenclatureEntity {
}
