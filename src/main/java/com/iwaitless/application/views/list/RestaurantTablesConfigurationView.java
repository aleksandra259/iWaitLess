package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.RestaurantTableService;
import com.iwaitless.application.views.MainLayout;
import com.iwaitless.application.views.forms.TablesForm;
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
import jakarta.annotation.security.PermitAll;

@PageTitle("Tables Configuration")
@Route(value = "tables-configuration", layout = MainLayout.class)
@PermitAll
public class RestaurantTablesConfigurationView extends VerticalLayout {

    RestaurantTableService restaurantTable;
    TextField filterText = new TextField();
    Grid<RestaurantTable> grid = new Grid<>(RestaurantTable.class, false);
    TablesForm form;

    public RestaurantTablesConfigurationView(RestaurantTableService restaurantTable) {
        this.restaurantTable = restaurantTable;

        addClassName("list-tables-view");
        setSizeFull();
        configureGrid();
        setTablesData();

        add(getToolbar(), grid);
    }


    private void configureGrid() {
        grid.addClassNames("tables-grid");
        grid.setSizeFull();
        grid.setAllRowsVisible(true);

        grid.addColumn(RestaurantTable::getTableNo).setHeader("Table No").setWidth("5em");
        grid.addColumn(RestaurantTable::getDescription).setHeader("Description").setWidth("30em");
        grid.addColumn(table ->
                (table.getQrCode() != null && !table.getQrCode().trim().isEmpty())
                        ? "Generated" : "Not Generated").setHeader("QR Code").setWidth("5em").setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, table) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_SMALL);
                    button.getElement().setAttribute("aria-label", "Edit table");
                    button.addClickListener(e ->
                            createTable(table));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
            })).setHeader("")
               .setWidth("2em")
               .setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, table) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_SMALL);
                    button.getElement().setAttribute("aria-label", "Delete table");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                    button.addClickListener(e ->
                            deleteTable(table));
            })).setHeader("")
               .setWidth("2em")
               .setTextAlign(ColumnTextAlign.END);

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

    private void deleteTable (RestaurantTable table) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle(
                String.format("Delete employee \"%s\"?", table.getTableId()));
        dialog.add("Are you sure you want to delete this table permanently?");

        Button deleteButton = new Button("Delete", (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);
        deleteButton.addClickListener(e -> {
            restaurantTable.deleteTable(table);
            setTablesData();
        });

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    private void closeEditor() {
        form.setTable(null);
        form.setVisible(false);
    }

}