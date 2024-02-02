package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.Users;
import com.iwaitless.application.services.StaffService;
import com.iwaitless.application.views.MainLayout;
import com.iwaitless.application.views.forms.EmployeeForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.text.SimpleDateFormat;

@PageTitle("StaffList")
@Route(value="", layout = MainLayout.class)
@PermitAll
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
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("staff-grid");
        grid.setSizeFull();

        grid.addColumn(createEmployeeRenderer()).setHeader("Employee");
        grid.addColumns("email", "phone", "address");
        grid.addColumn(employee -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    return formatter.format(employee.getBirthdate());
                })
                .setHeader("Birthdate");
        grid.addColumn(employee -> {
                    Users user = service.finsUserByEmployeeId(employee.getEmployeeId());
                    if (user != null)
                        return user.getUsername();

                    return null;
                })
                .setHeader("Username");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editEmployee(event.getValue()));
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
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add employee");
        addContactButton.addClickListener(click -> addEmployee());

        var toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(service.findAllEmployees(filterText.getValue()));
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new EmployeeForm(service.findAllRoles());
        form.setWidth("35em");
        form.addSaveListener(this::saveEmployee);
        form.addDeleteListener(this::deleteEmployee);
        form.addCloseListener(e -> closeEditor());
    }

    public void editEmployee(Staff staff) {
        if (staff == null) {
            closeEditor();
        } else {
            form.setContact(staff);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        editEmployee(new Staff());
    }

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        service.saveEmployee(event.getEmployee());
        updateList();
        closeEditor();
    }

    private void deleteEmployee(EmployeeForm.DeleteEvent event) {
        service.deleteEmployee(event.getEmployee());
        updateList();
        closeEditor();
    }
}
