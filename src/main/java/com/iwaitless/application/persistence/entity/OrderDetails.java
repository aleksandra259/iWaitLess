package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "ORDER_DETAILS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "DETAIL_ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long detailId;

    @ManyToOne
    @JoinColumn(name = "ORDER_NO")
    private Orders orderNo;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private MenuItems itemId;

    @NotNull
    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "COMMENT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    private OrderStatus status;
}
