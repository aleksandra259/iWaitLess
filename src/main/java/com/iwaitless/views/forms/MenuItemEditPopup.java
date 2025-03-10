package com.iwaitless.views.forms;

import com.iwaitless.persistence.entity.MenuItems;
import com.iwaitless.services.MenuItemService;
import com.iwaitless.views.utility.UploadImage;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import java.util.Currency;

public class MenuItemEditPopup extends FormLayout {

    TextField itemNameField = new TextField("Име");
    TextArea description = new TextArea("Описание");
    NumberField size = new NumberField("Размер");
    NumberField timeToProcess = new NumberField("Време на приготвяне");
    UploadImage image;
    NumberField price = new NumberField();
    Select<Currency> currency = new Select<>();
    Checkbox available = new Checkbox();

    BeanValidationBinder<MenuItems> binder = new BeanValidationBinder<>(MenuItems.class);
    Dialog dialog = new Dialog();

    Button save = new Button("Запази");
    Button cancel = new Button("Отказ");
    Button close = new Button(new Icon(VaadinIcon.CLOSE));

    public MenuItemEditPopup(MenuItems item,
                             MenuItemService menuItemService) {
        addClassName("menu-item-form");

        String header = item.getName();
        if (header == null || header.trim().isEmpty())
            dialog.setHeaderTitle("Нов артикул");
        else
            dialog.setHeaderTitle("Редактиране на артикул \"" + header + "\" ");

        dialog.add(new Span("Категория: " + item.getCategory().getName()));

        itemNameField.setRequired(true);
        description.setRequired(true);
        price.setRequired(true);
        size.setSuffixComponent(new Span("гр."));
        available.setLabel("Наличен ли е артикулът?");
        timeToProcess.setSuffixComponent(new Span("мин."));
        image = new UploadImage(item, menuItemService);

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
        binder.forField(timeToProcess).bind(MenuItems::getTimeToProcess, MenuItems::setTimeToProcess);
        binder.bind(price, MenuItems::getPrice, MenuItems::setPrice);
        binder.forField(currency).bind(MenuItems::getCurrency, MenuItems::setCurrency);
        binder.forField(available).bind(MenuItems::isAvailable, MenuItems::setAvailable);
        setItem(item);

        add(itemNameField, description, available, sizeAndTime, priceAndCurrency);
        addFormItem(priceAndCurrency, "Цена");
        addFormItem(available, "Наличност");
        addFormItem(image, "Изображения");
        getStyle().set("width", "25rem").set("max-width", "100%");

        dialog.add(this);

        createButtonsLayout();
        dialog.getFooter().add(cancel);
        dialog.getFooter().add(save);

        HorizontalLayout dialogHeader = new HorizontalLayout(close);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        dialogHeader.setWidthFull();
        dialog.getHeader().add(dialogHeader);
        dialog.setDraggable(true);

        dialog.open();
    }

    public void setItem(MenuItems item) {
        binder.setBean(item);
    }

    private void createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> {
            fireEvent(new MenuItemEditPopup.CloseEvent(this));
            dialog.close();
        });
        close.addClickListener(event -> {
            fireEvent(new MenuItemEditPopup.CloseEvent(this));
            dialog.close();
        });

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        setColspan(buttonLayout, 2);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new MenuItemEditPopup.SaveEvent(this, binder.getBean()));
            dialog.close();
        }
    }

    public void setMenuItem(MenuItems item) {
        binder.setBean(item);
    }


    // Events
    public static abstract class MenuItemFormEvent extends ComponentEvent<MenuItemEditPopup> {
        private final MenuItems item;

        protected MenuItemFormEvent(MenuItemEditPopup source, MenuItems item) {
            super(source, false);
            this.item = item;
        }

        public MenuItems getMenuItem() {
            return item;
        }
    }

    public static class SaveEvent extends MenuItemFormEvent {
        SaveEvent(MenuItemEditPopup source, MenuItems item) {
            super(source, item);
        }
    }

    public static class CloseEvent extends MenuItemFormEvent {
        CloseEvent(MenuItemEditPopup source) {
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

