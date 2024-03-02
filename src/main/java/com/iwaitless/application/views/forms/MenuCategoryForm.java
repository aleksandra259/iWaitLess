package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

public class MenuCategoryForm extends FormLayout {
    TextField nameField = new TextField();
    IntegerField orderNo = new IntegerField();

    BeanValidationBinder<MenuCategory> binder = new BeanValidationBinder<>(MenuCategory.class);
    Dialog dialog = new Dialog();

    Button save = new Button("Save");
    Button close = new Button("Cancel");

    public MenuCategoryForm(MenuCategory category) {
        addClassName("menu-category-form");

        String header = category.getName();
        if (header == null || header.trim().isEmpty())
            dialog.setHeaderTitle("New category");
        else
            dialog.setHeaderTitle("Edit category \"" + category.getName() + "\"");

        orderNo.setHelperText("Order of categories in the menu");
        orderNo.setValue(1);
        orderNo.setStepButtonsVisible(true);

        binder.bind(nameField, MenuCategory::getName, MenuCategory::setName);
        binder.bind(orderNo, MenuCategory::getOrderNo, MenuCategory::setOrderNo);
        setCategory(category);

        addFormItem(nameField, "Category Name");
        addFormItem(orderNo, "Order");
        getStyle().set("width", "20rem").set("max-width", "100%");

        dialog.add(this);

        createButtonsLayout();
        dialog.getFooter().add(close);
        dialog.getFooter().add(save);

        dialog.open();
    }

    public void setCategory(MenuCategory category) {
        binder.setBean(category);
    }

    private void createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> {
            fireEvent(new MenuCategoryForm.CloseEvent(this));
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
            fireEvent(new MenuCategoryForm.SaveEvent(this, binder.getBean()));
            dialog.close();
        }
    }

    public void setMenuCategory(MenuCategory category) {
        binder.setBean(category);
    }

    // Events
    public static abstract class MenuCategoryFormEvent extends ComponentEvent<MenuCategoryForm> {
        private final MenuCategory category;

        protected MenuCategoryFormEvent(MenuCategoryForm source, MenuCategory category) {
            super(source, false);
            this.category = category;
        }

        public MenuCategory getMenuCategory() {
            return category;
        }
    }

    public static class SaveEvent extends MenuCategoryFormEvent {
        SaveEvent(MenuCategoryForm source, MenuCategory category) {
            super(source, category);
        }
    }

    public static class CloseEvent extends MenuCategoryFormEvent {
        CloseEvent(MenuCategoryForm source) {
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

