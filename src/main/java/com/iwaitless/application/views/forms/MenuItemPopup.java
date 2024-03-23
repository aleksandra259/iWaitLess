package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.views.utility.UploadImage;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MenuItemPopup extends FormLayout {

    Dialog dialog = new Dialog();
    Button close = new Button(new Icon(VaadinIcon.CLOSE));

    MenuItems item;
    RestaurantTable table;

    public MenuItemPopup(MenuItems item,
                         RestaurantTable table) {
        this.item = item;
        this.table = table;

        addClassName("menu-item-popup");
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.START, LumoUtility.BorderRadius.NONE,
                LumoUtility.Margin.NONE);

        Div output = new Div();
        UploadImage.showImagesByItem(item, output);

        dialog.setHeaderTitle(item.getName());

        Paragraph description = new Paragraph(item.getDescription());
        description.addClassNames(LumoUtility.FontSize.LARGE);

        Span size = new Span();
        Span sizeHeader = new Span("Size:" );
        sizeHeader.getStyle().set("font-size", "13.5px");
        size.getElement().setAttribute("theme", "size");
        size.setText(item.getSize() + " grams");
        VerticalLayout sizeLayout = new VerticalLayout(sizeHeader, size);
        sizeLayout.setSpacing(false);

        Span timeToProcess = new Span();
        Span timeToProcessHeader = new Span();
        timeToProcessHeader.getStyle().set("font-size", "13.5px");
        timeToProcess.getElement().setAttribute("theme", "time");
        if (item.getTimeToProcess() != null ) {
            timeToProcessHeader.setText("Time to process:");
            timeToProcess.setText(item.getTimeToProcess() + " minutes");
        }
        VerticalLayout timeToProcessLayout = new VerticalLayout(timeToProcessHeader, timeToProcess);
        timeToProcessLayout.setSpacing(false);
        timeToProcessLayout.setAlignItems(FlexComponent.Alignment.END);

        HorizontalLayout timeAndSize = new HorizontalLayout();
        timeAndSize.setWidthFull();
        timeAndSize.add(sizeLayout, timeToProcessLayout);
        timeAndSize.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        timeAndSize.setAlignItems(FlexComponent.Alignment.BASELINE);

        TextArea comments = new TextArea("Comments");
        comments.setWidthFull();
        comments.setMaxLength(2000);
        comments.setPlaceholder("Here add additional comments for your order..");

        VerticalLayout verticalLayout =
                new VerticalLayout(description, timeAndSize, output, comments);
        verticalLayout.setWidthFull();
        verticalLayout.setSpacing(false);
        add(verticalLayout);

        dialog.add(this);
        getStyle().set("width", "30rem").set("max-width", "100%");
        dialog.getFooter().add(createButtonsLayout());

        HorizontalLayout dialogHeader = new HorizontalLayout();
        dialogHeader.add(close);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        dialogHeader.setWidthFull();
        dialog.getHeader().add(dialogHeader);

        dialog.open();
    }


    private HorizontalLayout createButtonsLayout() {
        close.addClickShortcut(Key.ESCAPE);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        close.addClickListener(event -> {
            fireEvent(new MenuItemPopup.CloseEvent(this));
            dialog.close();
        });

        Button addToCart = new Button( "Add to Cart",
                new Icon(VaadinIcon.CART));
        addToCart.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addToCart.setWidth("45%");
        addToCart.getStyle().set("border-radius", "0px");

        Button priceButton = new Button();
        priceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        priceButton.setText(item.getPrice() + " " + item.getCurrency());
        priceButton.setWidth("30%");
        priceButton.getStyle().set("border-radius", "0px");

        if (table == null || table.getTableId() == null) {
            addToCart.setEnabled(false);
            priceButton.setEnabled(false);
        }

        IntegerField quantity = new IntegerField();
        quantity.setValue(1);
        quantity.setStepButtonsVisible(true);
        quantity.setMin(0);
        quantity.setMax(9);
        quantity.setWidth("25%");

        HorizontalLayout buttonLayout
                = new HorizontalLayout(quantity, addToCart, priceButton);
        buttonLayout.setSpacing(false);
        buttonLayout.setWidthFull();

        return buttonLayout;
    }

    // Events
    public static abstract class MenuItemPopupEvent extends ComponentEvent<MenuItemPopup> {
        private final MenuItems item;

        protected MenuItemPopupEvent(MenuItemPopup source, MenuItems item) {
            super(source, false);
            this.item = item;
        }

        public MenuItems getMenuItem() {
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


