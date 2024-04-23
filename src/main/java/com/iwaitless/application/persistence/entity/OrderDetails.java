package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private MenuItems item;

    @NotNull
    @Column(name = "QUANTITY")
    private int quantity;

    @Column(name = "COMMENT")
    private String comment;
}
