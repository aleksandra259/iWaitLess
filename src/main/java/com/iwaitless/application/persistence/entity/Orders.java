package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.PaymentWay;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

@Table(name = "ORDERS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "ORDER_NO", updatable = false, nullable = false, insertable = false, unique = true)
    private Long orderNo;

    @ManyToOne
    @JoinColumn(name = "TABLE_RELATION_ID")
    private TableEmployeeRelation tableRelationId;

    @NotNull
    @Column(name = "ORDERED_ON")
    private Timestamp orderedOn;

    @NotNull
    @OneToOne
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    private OrderStatus status;

    @OneToOne
    @JoinColumn(name = "PAYMENT_WAY", referencedColumnName = "ID")
    private PaymentWay paymentWay;
}