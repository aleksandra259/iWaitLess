package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.IngredientCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Table(name = "INGREDIENTS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Ingredients {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "INGREDIENTS_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "ID")
    private String ingredientId;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "CATEGORY", referencedColumnName = "ID")
    private IngredientCategory category;
}