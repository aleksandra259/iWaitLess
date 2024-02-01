package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "INGREDIENT_RELATION")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class IngredientsRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "INGREDIENT_RELATION_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "INGREDIENT_RELATION_ID")
    private Long id;

    @ManyToMany
    @JoinTable(name = "MENU_ITEMS",
            joinColumns = @JoinColumn(name = "ITEM_ID"))
    private List<MenuItem> itemId;


    @ManyToMany
    @JoinTable(name = "MENU_ITEMS",
            joinColumns = @JoinColumn(name = "INGREDIENT_ID"))
    private List<Ingredients> ingredientId;
}
