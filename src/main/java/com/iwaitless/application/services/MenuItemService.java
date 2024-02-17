package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.MenuItem;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.persistence.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;



    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> findAllItems() {
        return menuItemRepository.findAll();
    }
    public List<MenuItem> findItemsByCategory(MenuCategory menuCategory) {
        if (menuCategory == null)
            return null;
        if (menuCategory.getId() == null)
            return null;

        return menuItemRepository
                .findAll()
                .stream()
                .filter(item -> menuCategory.getId().equals(item.getCategory().getId()))
                .collect(Collectors.toList());
    }

    public void deleteItem(MenuItem item) {
        menuItemRepository.delete(item);
    }

    public void saveItem(MenuItem item) {
        if (item == null) {
            System.err.println("Category save failed");
            return;
        }
        menuItemRepository.save(item);
    }



}