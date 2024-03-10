package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.views.utility.UploadImage;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import java.util.Currency;

public class MenuItemForm extends FormLayout {
    TextField itemNameField = new TextField("Name");
    TextArea description = new TextArea("Description");
    NumberField size = new NumberField("Size");
    NumberField timeToProcess = new NumberField("Time to process");
    UploadImage image;
    NumberField price = new NumberField();
    Select<Currency> currency = new Select<>();
    Checkbox available = new Checkbox();

    BeanValidationBinder<MenuItems> binder = new BeanValidationBinder<>(MenuItems.class);
    Dialog dialog = new Dialog();

    Button save = new Button("Save");
    Button close = new Button("Cancel");

    public MenuItemForm(MenuItems item) {
        addClassName("menu-item-form");

        String header = item.getName();
        if (header == null || header.trim().isEmpty())
            dialog.setHeaderTitle("New item");
        else
            dialog.setHeaderTitle("Edit item \"" + header + "\" ");

        dialog.add(new H4(item.getCategory().getName()));

        itemNameField.setRequired(true);
        description.setRequired(true);
        price.setRequired(true);
        size.setSuffixComponent(new Span("grams"));
        available.setLabel("Is item available?");
        timeToProcess.setSuffixComponent(new Span("minutes"));
        image = new UploadImage(item);

        currency.setItems(Currency.getInstance("BGN"),
                Currency.getInstance("EUR"));
        currency.getElement().executeJs(
                "this.focusElement.setAttribute('title', 'Currency')");

        HorizontalLayout priceAndCurrency = new HorizontalLayout(price, currency);
        priceAndCurrency.getThemeList().add("spacing-s");

        HorizontalLayout sizeAndTime = new HorizontalLayout(size, timeToProcess);
        sizeAndTime.getThemeList().add("spacing-s");

        setResponsiveSteps(new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP));

        binder.bind(itemNameField, MenuItems::getName, MenuItems::setName);
        binder.bind(description, MenuItems::getDescription, MenuItems::setDescription);
        binder.bind(size, MenuItems::getSize, MenuItems::setSize);
        binder.bind(timeToProcess, MenuItems::getTimeToProcess, MenuItems::setTimeToProcess);
        binder.bind(price, MenuItems::getPrice, MenuItems::setPrice);
        binder.forField(currency).bind(MenuItems::getCurrency, MenuItems::setCurrency);
        binder.forField(available).bind(MenuItems::isAvailable, MenuItems::setAvailable);
        setItem(item);

        add(itemNameField, description, available, sizeAndTime, priceAndCurrency);
        addFormItem(priceAndCurrency, "Price");
        addFormItem(available, "Availability");
        addFormItem(image, "Image");
        getStyle().set("width", "25rem").set("max-width", "100%");

        dialog.add(this);

        createButtonsLayout();
        dialog.getFooter().add(close);
        dialog.getFooter().add(save);

        dialog.open();
    }

    public void setItem(MenuItems item) {
        binder.setBean(item);
    }

    private void createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> {
            fireEvent(new MenuItemForm.CloseEvent(this));
            dialog.close();
        });

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(save, close);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        setColspan(buttonLayout, 2);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new MenuItemForm.SaveEvent(this, binder.getBean()));
            dialog.close();
        }
    }

    public void setMenuItem(MenuItems item) {
        binder.setBean(item);
    }

    // Events
    public static abstract class MenuItemFormEvent extends ComponentEvent<MenuItemForm> {
        private final MenuItems item;

        protected MenuItemFormEvent(MenuItemForm source, MenuItems item) {
            super(source, false);
            this.item = item;
        }

        public MenuItems getMenuItem() {
            return item;
        }
    }

    public static class SaveEvent extends MenuItemFormEvent {
        SaveEvent(MenuItemForm source, MenuItems item) {
            super(source, item);
        }
    }

    public static class CloseEvent extends MenuItemFormEvent {
        CloseEvent(MenuItemForm source) {
            super(source, null);
        }
    }

    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }
    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }
}

