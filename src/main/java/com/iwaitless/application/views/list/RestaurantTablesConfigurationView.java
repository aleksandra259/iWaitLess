package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.RestaurantTableService;
import com.iwaitless.application.views.MainLayout;
import com.iwaitless.application.views.forms.TablesForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Tables Configuration")
@Route(value = "tables-configuration", layout = MainLayout.class)
@PermitAll
public class RestaurantTablesConfigurationView extends VerticalLayout {

    RestaurantTableService restaurantTable;
    RestaurantTable table;
    TextField filterText = new TextField();
    Grid<RestaurantTable> grid = new Grid<>(RestaurantTable.class, false);
    TablesForm form;

    public RestaurantTablesConfigurationView(RestaurantTableService restaurantTable) {
        this.restaurantTable = restaurantTable;
        table = new RestaurantTable();

        addClassName("list-tables-view");
        setSizeFull();
        configureGrid();
        setTablesData();

        add(getToolbar(), grid);
    }


    private void configureGrid() {
        grid.addClassNames("staff-grid");
        grid.setSizeFull();

        grid.addColumns("tableId", "description");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by description...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> setTablesData());

        Button addContactButton = new Button("Add table");
        addContactButton.addClickListener(click -> createTable(new RestaurantTable()));

        var toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void setTablesData() {
        grid.setItems(restaurantTable.findAllTables(filterText.getValue()));
    }

    private void createTable(RestaurantTable table) {
        form = new TablesForm(table);
        form.addSaveListener(this::saveTable);
        form.addCloseListener(e -> closeEditor());

        setTablesData();
    }

    private void saveTable(TablesForm.SaveEvent event) {
        restaurantTable.saveTable(event.getRestaurantTable());
        setTablesData();
        closeEditor();
    }

    private void closeEditor() {
        form.setTable(null);
        form.setVisible(false);
    }

}