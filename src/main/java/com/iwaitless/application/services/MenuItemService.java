package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.persistence.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Value("${iwaitless.constant.images.directory}")
    public String menuDirectory;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItems> findAvailableItems() {
        return menuItemRepository.findAll()
                    .stream()
                    .filter(MenuItems::isAvailable)
                    .collect(Collectors.toList());
    }

    public MenuItems findItemById(Long itemId) {
        return menuItemRepository.findById(itemId).orElse(null);
    }

    public List<MenuItems> findItemsByCategory(MenuCategory menuCategory,
                                               String stringFilter) {
        if (menuCategory == null)
            return null;
        if (menuCategory.getId() == null)
            return null;

        if (stringFilter == null || stringFilter.isEmpty()) {
            return menuItemRepository
                    .findAll()
                    .stream()
                    .filter(item -> menuCategory.getId().equals(item.getCategory().getId()))
                    .collect(Collectors.toList());
        } else {
            return menuItemRepository
                    .search(stringFilter)
                    .stream()
                    .filter(item -> menuCategory.getId().equals(item.getCategory().getId()))
                    .collect(Collectors.toList());
        }
    }

    public List<MenuItems> findAvailableItemsByCategory(MenuCategory menuCategory,
                                                        String stringFilter) {
        if (menuCategory == null)
            return null;
        if (menuCategory.getId() == null)
            return null;

        if (stringFilter == null || stringFilter.isEmpty()) {
            return menuItemRepository
                    .findAll()
                    .stream()
                    .filter(item -> menuCategory.getId().equals(item.getCategory().getId()))
                    .filter(MenuItems::isAvailable)
                    .collect(Collectors.toList());
        } else {
            return menuItemRepository
                    .search(stringFilter)
                    .stream()
                    .filter(item -> menuCategory.getId().equals(item.getCategory().getId()))
                    .filter(MenuItems::isAvailable)
                    .collect(Collectors.toList());
        }
    }

    public void deleteItem(MenuItems item) {
        menuItemRepository.delete(item);
    }

    public void saveItem(MenuItems item) {
        if (item == null) {
            System.err.println("Category save failed");
            return;
        }
        menuItemRepository.save(item);
    }
}