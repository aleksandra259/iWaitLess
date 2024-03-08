package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItem;
import com.iwaitless.application.views.list.MenuItemsView;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MenuItemPopup extends FormLayout {

    Dialog dialog = new Dialog();

    Button close = new Button("Close");

    public MenuItemPopup(MenuItem item) {
        addClassName("menu-item-popup");
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START,
                LumoUtility.Padding.XSMALL, LumoUtility.BorderRadius.NONE);

        Image image = MenuItemsView.returnImage(item);
        image.setWidth("100%");

        Span header = new Span();
        header.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.Padding.Bottom.NONE);
        header.setText(item.getName());

        Paragraph description = new Paragraph(item.getDescription());
        description.addClassNames(LumoUtility.Margin.Vertical.SMALL, LumoUtility.Padding.Top.NONE,
                LumoUtility.Margin.Bottom.LARGE);

        Span size = new Span();
        size.getElement().setAttribute("theme", "size");
        size.setText(item.getSize() + " grams");

        TextArea comments = new TextArea("Comments");
        comments.setWidthFull();
        comments.setMaxLength(2000);
        comments.setPlaceholder("Here add additional comments for your order..");

//        Span price = new Span();
//        price.getElement().setAttribute("theme", "price");
//        price.setText(item.getPrice() + " " + item.getCurrency());


        VerticalLayout verticalLayout =
                new VerticalLayout(header, description, size, image, comments);
        verticalLayout.setWidthFull();
        add(verticalLayout);

        dialog.add(this);

        getStyle().set("width", "30rem").set("max-width", "100%");
        createButtonsLayout();
        dialog.getFooter().add(close);

        dialog.open();


    }


    private void createButtonsLayout() {
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        close.addClickListener(event -> {
            fireEvent(new MenuItemPopup.CloseEvent(this));
            dialog.close();
        });
    }

    // Events
    public static abstract class MenuItemPopupEvent extends ComponentEvent<MenuItemPopup> {
        private final MenuItem item;

        protected MenuItemPopupEvent(MenuItemPopup source, MenuItem item) {
            super(source, false);
            this.item = item;
        }

        public MenuItem getMenuItem() {
            return item;
        }
    }

    public static class CloseEvent extends MenuItemPopupEvent {
        CloseEvent(MenuItemPopup source) {
            super(source, null);
        }
    }

    public void addCloseListener(ComponentEventListener<MenuItemPopup.CloseEvent> listener) {
        addListener(MenuItemPopup.CloseEvent.class, listener);
    }
}


