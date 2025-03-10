package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.persistence.repository.MenuItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return menuItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found with ID: " + itemId));
    }

    public List<MenuItems> findItemsByCategory(MenuCategory menuCategory, String stringFilter) {
        if (menuCategory == null || menuCategory.getId() == null) {
            throw new IllegalArgumentException("Menu category is required and must have a valid ID.");
        }

        Stream<MenuItems> itemsStream = (stringFilter == null || stringFilter.isEmpty())
                ? menuItemRepository.findAll().stream()
                : menuItemRepository.search(stringFilter).stream();

        return itemsStream.filter(item -> menuCategory.getId().equals(item.getCategory().getId()))
                .collect(Collectors.toList());
    }

    public List<MenuItems> findAvailableItemsByCategory(MenuCategory menuCategory, String stringFilter) {
        if (menuCategory == null || menuCategory.getId() == null) {
            throw new IllegalArgumentException("Menu category is required and must have a valid ID.");
        }

        Stream<MenuItems> itemsStream = (stringFilter == null || stringFilter.isEmpty())
                ? menuItemRepository.findAll().stream()
                : menuItemRepository.search(stringFilter).stream();

        return itemsStream.filter(item -> menuCategory.getId().equals(item.getCategory().getId()))
                .filter(MenuItems::isAvailable)
                .collect(Collectors.toList());
    }

    public void deleteItem(MenuItems item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot delete a null item.");
        }
        menuItemRepository.delete(item);
    }

    public void saveItem(MenuItems item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot save a null item.");
        }
        menuItemRepository.save(item);
    }
}