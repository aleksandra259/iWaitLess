package com.iwaitless.application.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Table(name = "RESTAURANT_TABLE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class RestaurantTable {

    @Id
    @Column(name = "TABLE_ID", updatable = false, nullable = false, insertable = false, unique = true)
    private String tableId;

    @Column(name = "TABLE_NO")
    private String tableNo;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "QR_CODE")
    private String qrCode;
}