package com.iwaitless.views;

import com.iwaitless.persistence.entity.MenuItems;
import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.services.MenuItemService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import java.util.List;

public class MenuItemsPreviewView extends Main
        implements HasComponents, HasStyle {

    private final MenuItemService menuItem;
    private final List<MenuCategory> categorySorted;
    private final String searchField;
    private final RestaurantTable table;
    private final boolean showVegetarian;
    private final boolean showVegan;


    public MenuItemsPreviewView(MenuItemService menuItem,
                                List<MenuCategory> categorySorted,
                                String searchField,
                                RestaurantTable table,
                                boolean isVegetarian,
                                boolean isVegan) {
        this.menuItem = menuItem;
        this.categorySorted = categorySorted;
        this.searchField = searchField;
        this.table = table;
        this.showVegetarian = isVegetarian;
        this.showVegan = isVegan;

        addClassNames("image-gallery-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO,
                Margin.Bottom.LARGE, Padding.Horizontal.SMALL);

        setMenuItemsData();
    }

    private void setMenuItemsData() {
        removeAll();

        VaadinSession vaadinSession = VaadinSession.getCurrent();
        String consistFilterObject = (String)vaadinSession.getAttribute("consistFilter");
        String[] consistElements;
        if (consistFilterObject != null && !consistFilterObject.isEmpty())
            consistElements = consistFilterObject.split(",\\s*");
        else {
            consistElements = new String[0];
        }

        String notConsistFilterObject = (String)vaadinSession.getAttribute("notConsistFilter");
        String[] notConsistElements;
        if (notConsistFilterObject != null && !notConsistFilterObject.isEmpty())
            notConsistElements = notConsistFilterObject.split(",\\s*");
        else {
            notConsistElements = new String[0];
        }

        categorySorted.forEach(category -> {
            final int[] counter = {0};
            List<MenuItems> items = menuItem
                    .findAvailableItemsByCategory(category, searchField);

            Div categorySection = new Div();
            categorySection.setId(MenuPreviewView.createAnchorLink(category.getId()));
            categorySection.addClassNames("image-gallery-view", "menu-items-by-category");
            categorySection.addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Horizontal.AUTO,
                    LumoUtility.Margin.Bottom.LARGE, LumoUtility.Padding.Horizontal.SMALL);


            if (!items.isEmpty()) {
                OrderedList imageContainer = new OrderedList();
                imageContainer.addClassNames(LumoUtility.Gap.XSMALL, LumoUtility.Display.GRID,
                        LumoUtility.ListStyleType.NONE, Margin.NONE, Padding.NONE,
                        Padding.Bottom.LARGE);

                items.forEach(item -> {
                    if (item.isAvailable()
                            && ((item.isVegetarian() && showVegetarian)
                                || (item.isVegan() && showVegan)
                                || (!showVegetarian && !showVegan))) {

                        for (String element : consistElements) {
                            if (!item.getDescription().contains(element))
                                return;
                        }
                        for (String element : notConsistElements) {
                            if (item.getDescription().contains(element))
                                return;
                        }

                        imageContainer.add(new MenuItemViewCard(item, table));
                        counter[0] = counter[0] + 1;
                    }
                });

                if (counter[0] > 0) {
                    categorySection.add(new H3(category.getName()), imageContainer);
                    add(categorySection);
                }
            }
        });
    }
}