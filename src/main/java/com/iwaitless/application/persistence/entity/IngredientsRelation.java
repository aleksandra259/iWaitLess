package com.iwaitless.application.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table(name = "INGREDIENT_RELATION")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class IngredientsRelation {

    @ManyToMany
    @JoinColumn(name = "ITEM_ID")
    private MenuItem itemId;

    @ManyToMany
    @JoinColumn(name = "INGREDIENT_ID", referencedColumnName = "ID")
    private Ingredients ingredientId;
}
