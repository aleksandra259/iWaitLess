package com.iwaitless.views.forms;

import com.iwaitless.persistence.entity.MenuItems;
import com.iwaitless.persistence.entity.MenuItemsOrder;
import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.views.utility.UploadImage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
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
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

public class MenuItemPopup extends FormLayout {

    Dialog dialog = new Dialog();
    Button close = new Button(new Icon(VaadinIcon.CLOSE));

    private final MenuItems item;
    private final RestaurantTable table;

    TextArea comments = new TextArea("Коментар");
    MenuItemsOrder itemOrder = new MenuItemsOrder();


    public MenuItemPopup(MenuItems item,
                         RestaurantTable table) {
        this.item = item;
        this.table = table;

        dialog.addClassName("menu-item-popup");
        dialog.setHeaderTitle(item.getName());
        dialog.add(configureLayout());
        dialog.getFooter().add(createButtonsLayout());

        HorizontalLayout dialogHeader = new HorizontalLayout(close);
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


        VaadinSession session = VaadinSession.getCurrent();
        Object cartItemsObject = session.getAttribute("cartItems");
        List<MenuItemsOrder> cartItems;
        if (!(cartItemsObject instanceof List)) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        } else {
            cartItems = (List<MenuItemsOrder>) cartItemsObject;
        }

        IntegerField quantity = new IntegerField();
        quantity.addClassName("quantity-button");
        quantity.setValue(1);
        quantity.setStepButtonsVisible(true);
        quantity.setMin(0);
        quantity.setMax(9);

        Button addToCart = new Button( "Добави",
                new Icon(VaadinIcon.CART));
        addToCart.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addToCart.addClassName("add-to-cart-button");
        addToCart.addClickListener(event -> {
            setItemOrderData(comments.getValue(), quantity.getValue());
            cartItems.add(itemOrder);
        });

        Button priceButton = new Button();
        priceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        priceButton.addClassName("price-button");
        priceButton.setText(String.format("%.2f", item.getPrice()) + " " + item.getCurrency());

        if (table == null || table.getTableId() == null) {
            addToCart.setEnabled(false);
            priceButton.setEnabled(false);
        }

        HorizontalLayout buttonLayout
                = new HorizontalLayout(quantity, addToCart, priceButton);
        buttonLayout.setSpacing(false);
        buttonLayout.setWidthFull();

        return buttonLayout;
    }

    private void setItemOrderData(String comment, int count) {
        itemOrder.setItemId(item.getItemId());
        itemOrder.setName(item.getName());
        itemOrder.setDescription(item.getDescription());
        itemOrder.setCategory(item.getCategory());
        itemOrder.setPrice(item.getPrice());
        itemOrder.setCurrency(item.getCurrency());
        itemOrder.setSize(item.getSize());
        itemOrder.setTimeToProcess(item.getTimeToProcess());
        itemOrder.setComment(comment);
        itemOrder.setQuantity(count);
    }

    private Component configureLayout() {
        Div output = new Div();
        output.setWidthFull();
        UploadImage.showImagesByItem(item, output);

        Paragraph description = new Paragraph(item.getDescription());
        description.addClassNames(LumoUtility.FontSize.LARGE);

        final HorizontalLayout timeAndSize = getSizeAndTimeLayout();

        comments.setWidthFull();
        comments.setMaxLength(2000);
        comments.setPlaceholder("Тук добавете допълнителни коментари за вашата поръчка..");

        VerticalLayout verticalLayout =
                new VerticalLayout(description, timeAndSize, output, comments);
        verticalLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        verticalLayout.setSpacing(false);
        return verticalLayout;
    }

    private HorizontalLayout getSizeAndTimeLayout() {
        VerticalLayout sizeLayout = new VerticalLayout();
        if (item.getSize() != null)
            sizeLayout.add(new Span("Размер:" ),
                    new Span(String.format("%.0f", item.getSize()) + " гр."));
        sizeLayout.setSpacing(false);
        sizeLayout.setAlignItems(FlexComponent.Alignment.START);

        VerticalLayout timeToProcessLayout = new VerticalLayout();
        if (item.getTimeToProcess() != null) {
            timeToProcessLayout.add(new Span("Време за приготвяне:"),
                    new Span(String.format("%.0f", item.getTimeToProcess()) + " мин."));

            timeToProcessLayout.setSpacing(false);
            timeToProcessLayout.setAlignItems(FlexComponent.Alignment.END);
        }

        HorizontalLayout timeAndSize = new HorizontalLayout(sizeLayout, timeToProcessLayout);
        timeAndSize.setWidthFull();

        return timeAndSize;
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
}


