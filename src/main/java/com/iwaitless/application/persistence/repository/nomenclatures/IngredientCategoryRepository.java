package com.iwaitless.application.persistence.repository.nomenclatures;


import com.iwaitless.application.persistence.entity.nomenclatures.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, String> {

}
