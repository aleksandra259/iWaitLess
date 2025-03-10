package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.persistence.repository.nomenclatures.MenuCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;

    public MenuCategoryService(MenuCategoryRepository menuCategoryRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
    }

    public List<MenuCategory> findAllCategories() {
        return menuCategoryRepository.findAll();
    }

    public void deleteCategory(MenuCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Cannot delete a null category.");
        }
        menuCategoryRepository.delete(category);
    }

    public void saveCategory(MenuCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Cannot save a null category.");
        }
        menuCategoryRepository.save(category);
    }
}