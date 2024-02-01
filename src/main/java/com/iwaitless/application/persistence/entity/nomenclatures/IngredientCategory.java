package com.iwaitless.application.persistence.entity.nomenclatures;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Table(name = "HT_INGREDIENT_CATEGORY")
@Entity
@AllArgsConstructor
public class IngredientCategory extends AbstractNomenclatureEntity {

}
