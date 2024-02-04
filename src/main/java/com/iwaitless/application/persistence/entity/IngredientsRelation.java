package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Table(name = "INGREDIENT_RELATION")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class IngredientsRelation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "INGREDIENT_RELATION_ID", updatable = false, nullable = false, insertable = false, unique = true)
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
