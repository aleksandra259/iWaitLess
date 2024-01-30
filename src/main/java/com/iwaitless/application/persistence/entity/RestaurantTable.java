package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import lombok.*;


@Table(name = "RESTAURANT_TABLE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "RESTAURANT_TABLE_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "TABLE_ID")
    private Long tableId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "QR_CODE")
    private String qrCode;
}