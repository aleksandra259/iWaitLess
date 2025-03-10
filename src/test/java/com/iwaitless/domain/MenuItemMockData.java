package com.iwaitless.domain;

import com.iwaitless.persistence.entity.MenuItems;

import java.util.Currency;

public class MenuItemMockData {
    private MenuItemMockData() {}

    public static MenuItems initAvailableMenuItem() {
        MenuItems availableItem = new MenuItems(1L, "Item 1", "test", null,
                25d, Currency.getInstance("EUR"), 250d, null,
                true, false, false);
        availableItem.setCategory(MenuCategoryMockData.initMenuCategory());

        return availableItem;
    }
    public static MenuItems initUnavailableMenuItem() {
        MenuItems unavailableItem = new MenuItems(2L, "Item 2", "test", null,
                25d, Currency.getInstance("EUR"), 250d, null,
                false, false, false);
        unavailableItem.setCategory(MenuCategoryMockData.initMenuCategory());

        return unavailableItem;
    }
}
