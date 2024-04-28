package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.UserStaffRelation;
import com.iwaitless.application.services.StaffService;
import com.iwaitless.application.views.MainLayout;
import com.iwaitless.application.views.forms.EmployeePopup;
import com.iwaitless.application.views.utility.Renderers;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@PageTitle("iWaitLess | Служители")
@Route(value="staff", layout = MainLayout.class)
@RolesAllowed("ROLE_ADMIN")
public class ListStaffView extends VerticalLayout {

    Grid<Staff> grid = new Grid<>(Staff.class, false);
    TextField filterText = new TextField();
    EmployeePopup form;
    private final StaffService service;


    public ListStaffView(StaffService service) {
        this.service = service;

        setSizeFull();
        configureGrid();
        setEmployeeData();

        add(getToolbar(), grid);
    }

    private void configureGrid() {
        grid.addClassNames("staff-grid");
        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_NO_BORDER);

        grid.addColumn(Renderers.createEmployeeRenderer())
                .setComparator(Staff::getFirstName).setHeader("Служител");
        grid.addColumn(Staff::getEmail).setComparator(Staff::getEmail)
                .setHeader("Имейл").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Staff::getPhone).setHeader("Телефонен номер").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Staff::getAddress).setHeader("Адрес").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(employee -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    return formatter.format(employee.getBirthdate());
                })
                .setHeader("Рождена дата")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(employee -> {
                    UserStaffRelation user = service.finsUserByEmployeeId(employee.getEmployeeId());
                    if (user != null)
                        return user.getUsername();

                    return null;
                })
                .setHeader("Потребителско име")
                .setComparator(Staff::getUsername)
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, employee) -> {
                    button.getElement().setAttribute("aria-label", "Редактиране на служител");
                    button.addClickListener(e -> createEmployee(employee));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                    button.addClassName("edit-button");
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, employee) -> {
                    button.getElement().setAttribute("aria-label", "Изтрий служител");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                    button.addClickListener(e -> deleteEmployee(employee));
                    button.addClassName("delete-button");
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Филтриране по име...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> setEmployeeData());

        Button addContactButton = new Button("Добави служител");
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
        form = new EmployeePopup(staff, service.findAllRoles());
        form.addSaveListener(this::saveEmployee);
        form.addCloseListener(e -> closeEditor());

        setEmployeeData();
    }

    private void saveEmployee(EmployeePopup.SaveEvent event) {
        service.saveEmployee(event.getEmployee());
        setEmployeeData();
        closeEditor();
    }

    private void deleteEmployee (Staff staff) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle(
                String.format("Изтрий служител \"%s\"?",
                        staff.getFirstName() + " " + staff.getLastName()));
        dialog.add("Сигурни ли сте, че искате да изтриете този служител завинаги?");

        Button deleteButton = new Button("Изтрий", (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);
        deleteButton.addClickListener(e -> {
            service.deleteEmployee(staff);
            setEmployeeData();
        });

        Button cancelButton = new Button("Отказ", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
    }
}
