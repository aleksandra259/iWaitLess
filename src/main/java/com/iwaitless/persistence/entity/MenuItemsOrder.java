package com.iwaitless.persistence.entity;

import com.iwaitless.persistence.entity.nomenclatures.MenuCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Currency;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class MenuItemsOrder {

    private Long itemId;
    private String name;
    private String description;
    private MenuCategory category;
    private Double price;
    private Currency currency;
    private Double size;
    private Double timeToProcess;
    private String comment;
    private int quantity;
}
