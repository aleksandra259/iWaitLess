package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.TableEmployeeRelation;
import com.iwaitless.application.services.RestaurantTableService;
import com.iwaitless.application.services.TableEmployeeRelationService;
import com.iwaitless.application.views.MainLayout;
import com.iwaitless.application.views.forms.TablesPopup;
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

@PageTitle("iWaitLess | Конфигурация на маси")
@Route(value = "tables-configuration", layout = MainLayout.class)
@RolesAllowed("ROLE_ADMIN")
public class RestaurantTablesConfigurationView extends VerticalLayout {

    private final RestaurantTableService restaurantTable;
    private final TableEmployeeRelationService tableRelationService;
    TextField filterText = new TextField();
    Grid<RestaurantTable> grid = new Grid<>(RestaurantTable.class, false);
    TablesPopup form;

    public RestaurantTablesConfigurationView(RestaurantTableService restaurantTable,
                                             TableEmployeeRelationService tableRelationService) {
        this.restaurantTable = restaurantTable;
        this.tableRelationService = tableRelationService;

        addClassName("list-tables-view");
        setSizeFull();
        configureGrid();
        setTablesData();

        add(getToolbar(), grid);
    }


    private void configureGrid() {
        grid.addClassNames("tables-grid");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);

        grid.addColumn(RestaurantTable::getTableNo).setHeader("Номер на маса").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(RestaurantTable::getDescription).setHeader("Описание");
        grid.addColumn(table ->
                (table.getQrCode() != null && !table.getQrCode().trim().isEmpty())
                        ? "Генериран" : "Не е генериран").setHeader("QR код").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, table) -> {
                    button.getElement().setAttribute("aria-label", "Редактиране на маса");
                    button.addClickListener(e -> createTable(table));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                    button.addClassName("edit-button");
            })).setHeader("").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, table) -> {
                    button.getElement().setAttribute("aria-label", "Изтрий маса");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                    button.addClickListener(e -> deleteTable(table));
                    button.addClassName("delete-button");
            })).setHeader("").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Филтриране...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> setTablesData());

        Button addContactButton = new Button("Добави маса");
        addContactButton.addClickListener(click -> createTable(new RestaurantTable()));

        var toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void setTablesData() {
        grid.setItems(restaurantTable.findAllTables(filterText.getValue()));
    }

    private void createTable(RestaurantTable table) {
        form = new TablesPopup(table, restaurantTable);
        form.addSaveListener(this::saveTable);
        form.addCloseListener(e -> closeEditor());

        Staff employee = new Staff();
        employee.setEmployeeId(999999L);
        TableEmployeeRelation relation = new TableEmployeeRelation();
        relation.setTable(table);
        relation.setEmployee(employee);
        relation.setStatus("A");

        tableRelationService.assignTable(relation);

        setTablesData();
    }

    private void saveTable(TablesPopup.SaveEvent event) {
        restaurantTable.saveTable(event.getRestaurantTable());
        setTablesData();
        closeEditor();
    }

    private void deleteTable (RestaurantTable table) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle(
                String.format("Изтрий маса \"%s\"?", table.getTableId()));
        dialog.add("Сигурни ли сте, че искате да изтриете тази маса завинаги??");

        Button deleteButton = new Button("Изтрий", (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);
        deleteButton.addClickListener(e -> {
            restaurantTable.deleteTable(table);
            setTablesData();
        });

        Button cancelButton = new Button("Отказ", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    private void closeEditor() {
        form.setTable(null);
        form.setVisible(false);
    }

}