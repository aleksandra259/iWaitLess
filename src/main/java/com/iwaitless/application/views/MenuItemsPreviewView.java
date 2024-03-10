package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuItemService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.util.List;

public class MenuItemsPreviewView extends Main implements HasComponents, HasStyle {

    public MenuItemsPreviewView(MenuItemService menuItem,
                                List<MenuCategory> categorySorted,
                                String searchField) {

        addClassNames("image-gallery-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO,
                Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        categorySorted.forEach(category -> {
            List<MenuItems> items = menuItem
                    .findItemsByCategory(category, searchField);

            // Create a section with a corresponding ID
            Div categorySection = new Div();
            categorySection.setId(MenuPreviewView.createAnchorLink(category.getId()));
            H3 description = new H3(category.getName());
            description.addClassNames(Margin.Bottom.XSMALL, Margin.Top.XLARGE,
                    TextColor.HEADER, FontSize.XLARGE);
            categorySection.add(description);

            if (!items.isEmpty()) {
                OrderedList imageContainer = new OrderedList();
                imageContainer.addClassNames(Gap.LARGE, Display.GRID,
                        ListStyleType.NONE, Margin.NONE, Padding.NONE);

                items.forEach(item -> {
                    if (item.isAvailable())
                        imageContainer.add(new MenuItemViewCard(item));
                });

                categorySection.add(imageContainer);
                add(categorySection);
            }
        });
    }
}