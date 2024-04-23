package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.TableEmployeeRelation;
import com.iwaitless.application.services.RestaurantTableService;
import com.iwaitless.application.services.StaffService;
import com.iwaitless.application.services.TableEmployeeRelationService;
import com.iwaitless.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

@PageTitle("Tables Assignment")
@Route(value = "tables-assignment", layout = MainLayout.class)
@RolesAllowed("ROLE_USER_ST")
public class RestaurantTablesAssignView extends VerticalLayout {

    RestaurantTableService restaurantTable;
    TableEmployeeRelationService tableRelationService;
    StaffService staffService;

    private final String username;
    TextField filterText = new TextField();
    Grid<RestaurantTable> grid = new Grid<>(RestaurantTable.class, false);

    public RestaurantTablesAssignView(RestaurantTableService restaurantTable,
                                      TableEmployeeRelationService tableRelationService,
                                      StaffService staffService) {
        this.restaurantTable = restaurantTable;
        this.tableRelationService = tableRelationService;
        this.staffService = staffService;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.username = authentication.getName();

        addClassName("list-table-relations-view");
        setSizeFull();
        configureGrid();
        setTablesData();

        add(getToolbar(), grid);
    }


    private void configureGrid() {
        grid.addClassNames("tables-grid");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        getThemeList().add("spacing-xs");

        grid.addColumn(RestaurantTable::getTableId).setHeader("Table No").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(RestaurantTable::getDescription).setHeader("Description");
        grid.addColumn(table -> {
                String assigned = getAssignedEmployee(table);
                if (assigned != null && !assigned.trim().isEmpty())
                    return assigned;

                return "Not Assigned";
            }).setHeader("Assigned to")
              .setAutoWidth(true).setFlexGrow(0);


        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, table) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                            ButtonVariant.LUMO_SMALL);
                    button.setText("Assign to me");
                    button.getElement().setAttribute("aria-label", "Assign to me");
                    button.addClickListener(e ->
                            createTableRelation(table, staffService.findEmployeeByUsername(username)));

                    String assigned = getAssignedEmployee(table);
                    button.setEnabled(assigned == null || assigned.trim().isEmpty());
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, table) -> {
                    button.setText("Remove Assignment");
                    button.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                        ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                    button.getElement().setAttribute("aria-label", "Remove my Assignment");
                    button.addClickListener(e -> {
                            Staff staff = new Staff();
                            staff.setEmployeeId(999999L);
                            createTableRelation(table, staff);
                    });

                    String assigned = getAssignedEmployee(table);
                    Staff employee = staffService.findEmployeeByUsername(username);
                    button.setEnabled(assigned != null
                            && !assigned.trim().isEmpty()
                            && Objects.equals(assigned, employee.getFirstName() + " " + employee.getLastName()));
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
    }

    private void createTableRelation (RestaurantTable table, Staff employee) {
        TableEmployeeRelation relation = tableRelationService.findTableRelationByTable(table);
        relation.setStatus("D");
        tableRelationService.saveAssignTable(relation);

        relation = new TableEmployeeRelation();
        relation.setTable(table);
        relation.setEmployee(employee);
        relation.setStatus("A");

        tableRelationService.assignTable(relation);
        setTablesData();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by description...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> setTablesData());

        Button button = new Button("Remove my assignments");
        button.addClickListener(click -> refreshAssignments());

        var toolbar = new HorizontalLayout(filterText, button);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void setTablesData() {
        grid.setItems(restaurantTable.findAllTables(filterText.getValue()));
    }

    private void refreshAssignments() {
        List<RestaurantTable> assignedTables = tableRelationService
                .findAllAssignedTables(staffService.findEmployeeByUsername(username));

        Staff staff = new Staff();
        staff.setEmployeeId(999999L);
        assignedTables.forEach(table -> this.createTableRelation(table, staff));
    }

    private String getAssignedEmployee(RestaurantTable table) {
        Staff employee = tableRelationService.findAssignedTo(table);
        if (employee != null && employee.getEmployeeId() != null
                && !employee.getEmployeeId().toString().trim().isEmpty()
                && employee.getEmployeeId() != 999999L)
            return employee.getFirstName() + " " + employee.getLastName();

        return null;
    }

}