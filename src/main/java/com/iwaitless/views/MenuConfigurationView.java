package com.iwaitless.views;

import com.iwaitless.services.MenuCategoryService;
import com.iwaitless.services.MenuItemService;
import com.iwaitless.views.list.MenuCategoriesView;
import com.iwaitless.views.list.MenuItemsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("iWaitLess | Конфигуриране на маси")
@Route(value = "menu-configuration", layout = MainLayout.class)
@RolesAllowed("ROLE_ADMIN")
public class MenuConfigurationView extends VerticalLayout {

    private final MenuItemService menuItem;
    private final MenuCategoryService menuCategory;
    MenuItemsView items;
    MenuCategoriesView categories;


    public MenuConfigurationView(MenuItemService menuItem,
                                 MenuCategoryService menuCategory) {
        this.menuItem = menuItem;
        this.menuCategory = menuCategory;

        setSizeFull();
        configureMenuItems();
        configureMenuCategories();
        categories.setMenuCategoryData();

        add(getContent());
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(categories, items);
        content.setFlexGrow(1, categories);
        content.setFlexGrow(2, items);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureMenuItems() {
        items = new MenuItemsView(menuItem);
        items.setSizeFull();
        items.setWidth("50em");
    }
    private void configureMenuCategories() {
        categories = new MenuCategoriesView(menuCategory, menuItem, items);
        categories.setSizeFull();
        categories.setWidth("17em");
    }

}