package com.iwaitless.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;


@Table(name = "RESTAURANT_TABLE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "TABLE_ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long tableId;

    @Column(name = "TABLE_NO")
    private String tableNo;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "QR_CODE")
    private String qrCode;
}