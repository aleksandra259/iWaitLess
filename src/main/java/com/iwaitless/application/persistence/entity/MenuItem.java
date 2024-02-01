package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.vaadin.flow.component.html.Image;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Currency;

@Table(name = "MENU_ITEM")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "MENU_ITEM_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "ITEM_ID")
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

    @Column(name = "IMAGE")
    private Image image;
}
