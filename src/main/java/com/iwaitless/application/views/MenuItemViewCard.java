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
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN,
                AlignItems.START, Padding.SMALL, BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER,
                JustifyContent.CENTER, Margin.Bottom.NONE, Overflow.HIDDEN,
                BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("80px");

        Image image = MenuItemsView.returnImage(item);
        image.setWidth("100%");

        div.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.SMALL, FontWeight.SEMIBOLD, Margin.Bottom.NONE);
        header.setText(item.getName());

        Paragraph description = new Paragraph(item.getDescription());
        description.addClassNames(FontSize.XSMALL, Margin.Top.NONE, Padding.XSMALL);

        Span size = new Span();
        size.getElement().setAttribute("theme", "size");
        size.setText(item.getSize() + " grams");
        size.addClassNames(FontSize.XXSMALL);

        Span price = new Span();
        price.getElement().setAttribute("theme", "price");
        price.setText(item.getPrice() + " " + item.getCurrency());
        price.addClassNames(FontSize.XXSMALL);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(price, size);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(div, header, description, horizontalLayout);

        addClickListener(e -> new MenuItemPopup(item, table));

    }
}

