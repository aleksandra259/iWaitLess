package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

public class TablesForm extends FormLayout {
    TextField id = new TextField("ID");
    TextField description = new TextField("Description");

    BeanValidationBinder<RestaurantTable> binder = new BeanValidationBinder<>(RestaurantTable.class);
    Dialog dialog = new Dialog();

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public TablesForm(RestaurantTable table) {
        String header = String.valueOf(table.getTableId());
        if (header == null || header.trim().isEmpty())
            dialog.setHeaderTitle("Create new table");
        else
            dialog.setHeaderTitle("Edit table number " + header);


        binder.bindInstanceFields(this);
        setTable(table);

        add(id, description);
        getStyle().set("width", "25rem").set("max-width", "100%");

        dialog.add(this);

        createButtonsLayout();
        dialog.getFooter().add(close);
        dialog.getFooter().add(save);

        dialog.open();
    }

    private void createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        delete.getStyle().set("margin-inline-end", "auto");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(save, delete, close);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        setColspan(buttonLayout, 2);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
            dialog.close();
        }
    }

    public void setTable(RestaurantTable table) {
        binder.setBean(table);
    }

    // Events
    public static abstract class TablesFormEvent extends ComponentEvent<TablesForm> {
        private final RestaurantTable table;

        protected TablesFormEvent(TablesForm source, RestaurantTable table) {
            super(source, false);
            this.table = table;
        }

        public RestaurantTable getRestaurantTable() {
            return table;
        }
    }

    public static class SaveEvent extends TablesFormEvent {
        SaveEvent(TablesForm source, RestaurantTable table) {
            super(source, table);
        }
    }

    public static class DeleteEvent extends TablesFormEvent {
        DeleteEvent(TablesForm source, RestaurantTable table) {
            super(source, table);
        }

    }

    public static class CloseEvent extends TablesFormEvent {
        CloseEvent(TablesForm source) {
            super(source, null);
        }
    }

    public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        addListener(DeleteEvent.class, listener);
    }

    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }
    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }
}
