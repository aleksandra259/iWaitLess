package com.iwaitless.application.persistence.repository.nomenclatures;


import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, String> {

}
