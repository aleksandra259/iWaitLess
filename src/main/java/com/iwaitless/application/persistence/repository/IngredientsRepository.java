package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientsRepository extends JpaRepository<Ingredients, Long> {

}
