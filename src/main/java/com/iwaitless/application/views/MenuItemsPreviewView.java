package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuItemService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.util.List;

public class MenuItemsPreviewView extends Main
        implements HasComponents, HasStyle {

    public MenuItemsPreviewView(MenuItemService menuItem,
                                List<MenuCategory> categorySorted,
                                String searchField,
                                RestaurantTable table) {

        addClassNames("image-gallery-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO,
                Margin.Bottom.LARGE, Padding.Horizontal.LARGE);

        categorySorted.forEach(category -> {
            List<MenuItems> items = menuItem
                    .findAvailableItemsByCategory(category, searchField);

            // Create a section with a corresponding ID
            Div categorySection = new Div();
            categorySection.setId(MenuPreviewView.createAnchorLink(category.getId()));
            H1 description = new H1(category.getName());
            description.addClassNames(Margin.Bottom.XSMALL, Margin.Top.XLARGE,
                    TextColor.HEADER, FontSize.LARGE);
            categorySection.add(description);

            if (!items.isEmpty()) {
                OrderedList imageContainer = new OrderedList();
                imageContainer.addClassNames(Gap.SMALL, Display.GRID,
                        ListStyleType.NONE, Margin.NONE, Padding.NONE);

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