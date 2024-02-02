package com.iwaitless.application.views.list;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

//@PageTitle("MenuConfigurationList")
//@Route(value="", layout = "")
//@PermitAll
public class ListMenuConfigurationView extends VerticalLayout {
//    Grid<Staff> grid = new Grid<>(Staff.class);
//    TextField filterText = new TextField();
//    EmployeeForm form;
//    StaffService service;
//
//    public ListMenuConfigurationView(StaffService service) {
//        this.service = service;
//        addClassName("list-employee-view");
//        setSizeFull();
//        configureGrid();
//        configureForm();
//
//        add(getToolbar(), getContent());
//        updateList();
//        closeEditor();
//    }
//
//    private void configureGrid() {
//        grid.addClassNames("staff-grid");
//        grid.setSizeFull();
//        grid.setColumns("employeeId", "name", "email", "phone", "address");
//        grid.addColumn(employee -> employee.getRole().getName()).setHeader("Role");
//        grid.getColumns().forEach(col -> col.setAutoWidth(true));
//
//        grid.asSingleSelect().addValueChangeListener(event ->
//                editEmployee(event.getValue()));
//    }
//
//    private HorizontalLayout getToolbar() {
//        filterText.setPlaceholder("Filter by name...");
//        filterText.setClearButtonVisible(true);
//        filterText.setValueChangeMode(ValueChangeMode.LAZY);
//        filterText.addValueChangeListener(e -> updateList());
//
//        Button addContactButton = new Button("Add employee");
//        addContactButton.addClickListener(click -> addEmployee());
//
//        var toolbar = new HorizontalLayout(filterText, addContactButton);
//        toolbar.addClassName("toolbar");
//        return toolbar;
//    }
//
//    private void updateList() {
//        grid.setItems(service.findAllEmployees(filterText.getValue()));
//    }
//
//    private Component getContent() {
//        HorizontalLayout content = new HorizontalLayout(grid, form);
//        content.setFlexGrow(2, grid);
//        content.setFlexGrow(1, form);
//        content.addClassNames("content");
//        content.setSizeFull();
//        return content;
//    }
//
//    private void configureForm() {
//        form = new EmployeeForm(service.findAllRoles());
//        form.setWidth("25em");
//        form.addSaveListener(this::saveEmployee);
//        form.addDeleteListener(this::deleteEmployee);
//        form.addCloseListener(e -> closeEditor());
//    }
//
//    public void editEmployee(Staff staff) {
//        if (staff == null) {
//            closeEditor();
//        } else {
//            form.setContact(staff);
//            form.setVisible(true);
//            addClassName("editing");
//        }
//    }
//
//    private void closeEditor() {
//        form.setContact(null);
//        form.setVisible(false);
//        removeClassName("editing");
//    }
//
//    private void addEmployee() {
//        grid.asSingleSelect().clear();
//        editEmployee(new Staff());
//    }
//
//    private void saveEmployee(EmployeeForm.SaveEvent event) {
//        service.saveEmployee(event.getEmployee());
//        updateList();
//        closeEditor();
//    }
//
//    private void deleteEmployee(EmployeeForm.DeleteEvent event) {
//        service.deleteEmployee(event.getEmployee());
//        updateList();
//        closeEditor();
//    }
}
