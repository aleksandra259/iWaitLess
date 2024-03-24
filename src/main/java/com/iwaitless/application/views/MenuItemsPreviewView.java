package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuItemService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import java.util.List;

public class MenuItemsPreviewView extends Main
        implements HasComponents, HasStyle {

    MenuItemService menuItem;
    List<MenuCategory> categorySorted;
    String searchField;
    RestaurantTable table;

    public MenuItemsPreviewView(MenuItemService menuItem,
                                List<MenuCategory> categorySorted,
                                String searchField,
                                RestaurantTable table) {
        this.menuItem = menuItem;
        this.categorySorted = categorySorted;
        this.searchField = searchField;
        this.table = table;

        addClassNames("image-gallery-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO,
                Margin.Bottom.LARGE, Padding.Horizontal.SMALL);

        setMenuItemsData();
    }

    private void setMenuItemsData() {
        removeAll();

        categorySorted.forEach(category -> {
            List<MenuItems> items = menuItem
                    .findAvailableItemsByCategory(category, searchField);

            Div categorySection = new Div();
            categorySection.setId(MenuPreviewView.createAnchorLink(category.getId()));
            H1 description = new H1(category.getName());
            description.addClassNames(Margin.Bottom.XSMALL, Margin.Top.XLARGE,
                    LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE);
            categorySection.add(description);

            if (!items.isEmpty()) {
                OrderedList imageContainer = new OrderedList();
                imageContainer.addClassNames(LumoUtility.Gap.SMALL, LumoUtility.Display.GRID,
                        LumoUtility.ListStyleType.NONE, Margin.NONE, Padding.NONE);

                items.forEach(item -> {
                    if (item.isAvailable())
                        imageContainer.add(new MenuItemViewCard(item, table));
                });

                categorySection.add(imageContainer);
                add(categorySection);
            }
        });
    }
}