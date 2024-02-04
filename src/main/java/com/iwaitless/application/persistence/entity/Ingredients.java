package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.IngredientCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "INGREDIENTS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Ingredients {

    @Id
    @GenericGenerator(name = "Incremental")
    @Column(name = "ID", updatable = false, nullable = false, insertable = false, unique = true)
    private String ingredientId;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "CATEGORY", referencedColumnName = "ID")
    private IngredientCategory category;
}