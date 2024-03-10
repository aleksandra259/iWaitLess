package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.views.list.MenuItemsView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

public class MenuItemViewCard extends ListItem {

    public MenuItemViewCard(MenuItems item) {
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN,
                AlignItems.START, Padding.SMALL, BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER,
                JustifyContent.CENTER, Margin.Bottom.SMALL, Overflow.HIDDEN,
                BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("160px");

        Image image = MenuItemsView.returnImage(item);
        image.setWidth("100%");

        div.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.LARGE, FontWeight.SEMIBOLD, Padding.Bottom.NONE);
        header.setText(item.getName());

        Paragraph description = new Paragraph(item.getDescription());
        description.addClassNames(Margin.Vertical.SMALL, Padding.XSMALL);

        Span size = new Span();
        size.getElement().setAttribute("theme", "size");
        size.setText(item.getSize() + " grams");

        Span price = new Span();
        price.getElement().setAttribute("theme", "price");
        price.setText(item.getPrice() + " " + item.getCurrency());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(price, size);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(div, header, description, horizontalLayout);

        addClickListener(e -> new MenuItemPopup(item));

    }
}

