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

    public MenuCategory findCategoryByName(String name) {
        return menuCategoryRepository
                .findAll()
                .stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    public MenuCategory findCategoryById(String id) {
        return menuCategoryRepository
                .findById(id)
                .orElse(null);
    }

    public void deleteCategory(MenuCategory category) {
        menuCategoryRepository.delete(category);
    }

    public void saveCategory(MenuCategory category) {
        if (category == null) {
            System.err.println("Category save failed");
            return;
        }
        menuCategoryRepository.save(category);
    }



}