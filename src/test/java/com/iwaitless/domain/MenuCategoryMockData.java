package com.iwaitless.domain;

import com.iwaitless.persistence.entity.nomenclatures.MenuCategory;

public class MenuCategoryMockData {

    private MenuCategoryMockData() {}

    public static MenuCategory initMenuCategory() {
        MenuCategory category = new MenuCategory(1);
        category.setId("CAT_1");
        category.setName("Category name");

        return category;
    }
}
