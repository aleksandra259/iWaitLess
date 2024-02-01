package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.PaymentWay;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Table(name = "ORDERS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "ORDER_NO_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "ORDER_NO")
    private Long orderNo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "TABLE_REL_ID")
    private TableEmployeeRelation tableRelationId;

    @NotNull
    @Column(name = "ORDERED_ON")
    private Timestamp orderedOn;

    @NotNull
    @OneToOne
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    private OrderStatus status;

    @NotNull
    @OneToOne
    @JoinColumn(name = "PAYMENT_WAY", referencedColumnName = "ID")
    private PaymentWay paymentWay;
}