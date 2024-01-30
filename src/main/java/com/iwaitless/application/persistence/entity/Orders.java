package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.PaymentWay;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Table(name = "ORDER")
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
    @Column(name = "TABLE_REL_ID")
    private TableEmployeeRelation tableRelationId;

    @NotNull
    @Column(name = "ORDERED_ON")
    private Timestamp orderedOn;

    @NotNull
    @Column(name = "STATUS")
    @OneToOne
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    private OrderStatus status;

    @NotNull
    @Column(name = "PAYMENT_WAY")
    @OneToOne
    @JoinColumn(name = "PAYMENT_WAY", referencedColumnName = "ID")
    private PaymentWay paymentWay;
}