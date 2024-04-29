package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.views.forms.MenuItemPopup;
import com.iwaitless.application.views.list.MenuItemsView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

public class MenuItemViewCard extends ListItem {

    public MenuItemViewCard(MenuItems item,
                            RestaurantTable table) {
        addClassNames(Display.FLEX, FlexDirection.COLUMN,
                AlignItems.START, Padding.SMALL, BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(Display.FLEX, AlignItems.CENTER,
                JustifyContent.CENTER, Overflow.HIDDEN);
        div.addClassNames("menu-item-card");

        Image image = MenuItemsView.returnImage(item);
        image.addClassName("menu-item-image");

        div.add(image);

        Span header = new Span(item.getName());
        header.addClassNames(FontSize.MEDIUM, FontWeight.SEMIBOLD);

        add(div, header, getPriceSizeLayout(item));
        addClickListener(e -> new MenuItemPopup(item, table));
    }

    private static HorizontalLayout getPriceSizeLayout(MenuItems item) {
        Span size = new Span();
        if (item.getSize() != null && item.getSize() != 0.0d)
            size.setText(String.format("%.0f", item.getSize()) + " гр.");
        size.addClassName("menu-item-card");

        Span price = new Span();
        price.setText(String.format("%.2f", item.getPrice()) + " " + item.getCurrency());
        price.addClassName("menu-item-card");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(price, size);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        return horizontalLayout;
    }
}

