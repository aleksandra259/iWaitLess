package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Table(name = "ORDER_DETAILS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "ORDER_DTL_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "DETAIL_ID")
    private Long detailId;

    @ManyToOne
    @JoinColumn(name = "ORDER_NO")
    private Orders orderNo;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private MenuItem itemId;

    @NotNull
    private Long quantity;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    private OrderStatus status;
}
