package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

public class MenuCategoryPopup extends FormLayout {
    TextField nameField = new TextField();
    IntegerField orderNo = new IntegerField();

    BeanValidationBinder<MenuCategory> binder = new BeanValidationBinder<>(MenuCategory.class);
    Dialog dialog = new Dialog();

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button close = new Button(new Icon(VaadinIcon.CLOSE));

    public MenuCategoryPopup(MenuCategory category) {
        addClassName("menu-category-form");

        String header = category.getName();
        if (header == null || header.trim().isEmpty())
            dialog.setHeaderTitle("New category");
        else
            dialog.setHeaderTitle("Edit category \"" + category.getName() + "\"");

        orderNo.setHelperText("Order of categories in the menu");
        orderNo.setValue(1);
        orderNo.setStepButtonsVisible(true);
        nameField.setRequired(true);

        binder.bind(nameField, MenuCategory::getName, MenuCategory::setName);
        binder.bind(orderNo, MenuCategory::getOrderNo, MenuCategory::setOrderNo);
        setCategory(category);

        addFormItem(nameField, "Category Name");
        addFormItem(orderNo, "Order");
        getStyle().set("width", "20rem").set("max-width", "100%");

        dialog.add(this);

        createButtonsLayout();
        dialog.getFooter().add(cancel);
        dialog.getFooter().add(save);

        HorizontalLayout dialogHeader = new HorizontalLayout();
        dialogHeader.add(close);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        dialogHeader.setWidthFull();
        dialog.getHeader().add(dialogHeader);
        dialog.setDraggable(true);

        dialog.open();
    }

    public void setCategory(MenuCategory category) {
        binder.setBean(category);
    }

    private void createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> validateAndSave());
        save.addClickShortcut(Key.ENTER);

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> {
            fireEvent(new MenuCategoryPopup.CloseEvent(this));
            dialog.close();
        });

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        close.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        setColspan(buttonLayout, 2);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new MenuCategoryPopup.SaveEvent(this, binder.getBean()));
            dialog.close();
        }
    }

    public void setMenuCategory(MenuCategory category) {
        binder.setBean(category);
    }

    // Events
    public static abstract class MenuCategoryFormEvent extends ComponentEvent<MenuCategoryPopup> {
        private final MenuCategory category;

        protected MenuCategoryFormEvent(MenuCategoryPopup source, MenuCategory category) {
            super(source, false);
            this.category = category;
        }

        public MenuCategory getMenuCategory() {
            return category;
        }
    }

    public static class SaveEvent extends MenuCategoryFormEvent {
        SaveEvent(MenuCategoryPopup source, MenuCategory category) {
            super(source, category);
        }
    }

    public static class CloseEvent extends MenuCategoryFormEvent {
        CloseEvent(MenuCategoryPopup source) {
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

