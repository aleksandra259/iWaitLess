package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "QR_CODE")
    private String qrCode;
}