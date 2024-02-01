package com.iwaitless.application.persistence.entity.nomenclatures;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Table(name = "HT_ORDER_STATUS")
@Entity
@AllArgsConstructor
public class OrderStatus extends AbstractNomenclatureEntity {
}
