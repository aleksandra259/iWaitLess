package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Currency;

@Table(name = "MENU_ITEM")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class MenuItems {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "ITEM_ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long itemId;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "CATEGORY", referencedColumnName = "ID")
    private MenuCategory category;

    @NotNull
    @Column(name = "PRICE")
    private Double price;

    @NotNull
    @Column(name = "CURRENCY")
    private Currency currency;

    @Column(name = "SIZE")
    private Double size;

    @Column(name = "TIME_TO_PROCESS")
    private Double timeToProcess;

    @Column(name = "AVAILABLE")
    private boolean available;
}
