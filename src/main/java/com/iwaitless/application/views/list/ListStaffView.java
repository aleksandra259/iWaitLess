package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.UserStaffRelation;
import com.iwaitless.application.services.StaffService;
import com.iwaitless.application.views.MainLayout;
import com.iwaitless.application.views.forms.EmployeeForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@PageTitle("Staff List")
@Route(value="staff", layout = MainLayout.class)
@RolesAllowed("ROLE_ADMIN")
public class ListStaffView extends VerticalLayout {
    Grid<Staff> grid = new Grid<>(Staff.class, false);
    TextField filterText = new TextField();
    EmployeeForm form;
    StaffService service;

    public ListStaffView(StaffService service) {
        this.service = service;

        addClassName("list-employee-view");
        setSizeFull();
        configureGrid();
        setEmployeeData();

        add(getToolbar(), grid);
    }

    private void configureGrid() {
        grid.addClassNames("staff-grid");
        grid.setWidthFull();
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.addColumn(createEmployeeRenderer()).setHeader("Employee");
        grid.addColumn(Staff::getEmail).setHeader("Email").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Staff::getPhone).setHeader("Phone").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Staff::getAddress).setHeader("Address").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(employee -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    return formatter.format(employee.getBirthdate());
                })
                .setHeader("Birthdate")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(employee -> {
                    UserStaffRelation user = service.finsUserByEmployeeId(employee.getEmployeeId());
                    if (user != null)
                        return user.getUsername();

                    return null;
                })
                .setHeader("Username")
                .setAutoWidth(true).setFlexGrow(0);;
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, employee) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_SMALL);
                    button.getElement().setAttribute("aria-label", "Edit employee");
                    button.addClickListener(e ->
                            createEmployee(employee));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, employee) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_SMALL);
                    button.getElement().setAttribute("aria-label", "Delete employee");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                    button.addClickListener(e ->
                            deleteEmployee(employee));
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

    }

    private static Renderer<Staff> createEmployeeRenderer() {
        return LitRenderer.<Staff> of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<vaadin-avatar name=\"${item.name}\" alt=\"User avatar\"></vaadin-avatar>"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.name} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.role}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", employee -> employee.getFirstName() + " " + employee.getLastName())
                .withProperty("role", employee -> employee.getRole().getName());
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> setEmployeeData());

        Button addContactButton = new Button("Add employee");
        addContactButton.addClickListener(click -> createEmployee(new Staff()));

        var toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void setEmployeeData() {
        grid.setItems(service.findAllEmployees(filterText.getValue())
                .stream()
                .filter(e -> e.getEmployeeId() != 999999L)
                .collect(Collectors.toList()));
    }

    private void createEmployee(Staff staff) {
        form = new EmployeeForm(staff, service.findAllRoles());
        form.addSaveListener(this::saveEmployee);
        form.addCloseListener(e -> closeEditor());

        setEmployeeData();
    }

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        service.saveEmployee(event.getEmployee());
        setEmployeeData();
        closeEditor();
    }

    private void deleteEmployee (Staff staff) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle(
                String.format("Delete employee \"%s\"?",
                        staff.getFirstName() + " " + staff.getLastName()));
        dialog.add("Are you sure you want to delete this employee permanently?");

        Button deleteButton = new Button("Delete", (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);
        deleteButton.addClickListener(e -> {
            service.deleteEmployee(staff);
            setEmployeeData();
        });

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
    }
}
