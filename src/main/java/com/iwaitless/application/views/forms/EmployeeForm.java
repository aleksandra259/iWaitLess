package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.nomenclatures.StaffRole;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import java.util.List;

public class EmployeeForm extends FormLayout {
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("Email");
    TextField phone = new TextField("Phone number");
    TextArea address = new TextArea("Address");
    DatePicker birthdate = new DatePicker("Birthdate");
    ComboBox<StaffRole> role = new ComboBox<>("Role");

    BeanValidationBinder<Staff> binder = new BeanValidationBinder<>(Staff.class);
    Dialog dialog = new Dialog();

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public EmployeeForm(Staff staff,
                        List<StaffRole> roles) {
        String name = staff.getFirstName();
        if (name == null || name.trim().isEmpty())
            dialog.setHeaderTitle("New employee");
        else
            dialog.setHeaderTitle("Edit " + name +"'s profile");

        role.setItems(roles);
        role.setItemLabelGenerator(StaffRole::getName);

        firstName.setRequired(true);
        lastName.setRequired(true);
        email.setRequired(true);
        role.setRequired(true);

        binder.bindInstanceFields(this);
        setContact(staff);
        address.setWidthFull();

        add(firstName, lastName, email, phone, address, birthdate, role);
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new ResponsiveStep("320px", 2),
                // Use three columns, if the layout's width exceeds 500px
                new ResponsiveStep("500px", 3));
        setColspan(address, 2);
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

    public void setContact(Staff employee) {
        binder.setBean(employee);
    }

    // Events
    public static abstract class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {
        private final Staff staff;

        protected EmployeeFormEvent(EmployeeForm source, Staff staff) {
            super(source, false);
            this.staff = staff;
        }

        public Staff getEmployee() {
            return staff;
        }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, Staff staff) {
            super(source, staff);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, Staff staff) {
            super(source, staff);
        }

    }

    public static class CloseEvent extends EmployeeFormEvent {
        CloseEvent(EmployeeForm source) {
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
