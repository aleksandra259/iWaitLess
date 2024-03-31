package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.views.forms.MenuCategoryForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.Random;

public class MenuCategoriesView extends VerticalLayout {

    private final MenuCategoryService menuCategory;
    private final MenuItemService menuItem;
    private final MenuItemsView items;


    H2 categoriesHeader = new H2();
    Button newCategory = new Button();

    Grid<MenuCategory> grid = new Grid<>(MenuCategory.class, false);
    MenuCategoryForm form;

    public MenuCategoriesView(MenuCategoryService menuCategory,
                              MenuItemService menuItem,
                              MenuItemsView items) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.items = items;

        addClassName("categories-view");
        setSizeFull();
        configureGrid();

        newCategory.setText("New");
        newCategory.setWidth("min-content");
        newCategory.setMaxWidth("100px");
        newCategory.addClickListener(e -> createMenuCategory(getCategory()));

        categoriesHeader.setText("Categories");
        categoriesHeader.setWidth("max-content");
        categoriesHeader.setMaxWidth("150px");

        setMenuCategoryData();

        HorizontalLayout categories = new HorizontalLayout(categoriesHeader, newCategory);
        add(categories, grid);
    }

    private static MenuCategory getCategory() {
        MenuCategory category = new MenuCategory();
        String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random random = new Random();

        category.setId(candidateChars.charAt(random.nextInt(candidateChars.length()))
            + candidateChars.charAt(random.nextInt(candidateChars.length()))
            + candidateChars.charAt(random.nextInt(candidateChars.length()))
            + candidateChars.charAt(random.nextInt(candidateChars.length()))
            + "_" + candidateChars.charAt(random.nextInt(candidateChars.length())));

        return category;
    }

    private void createMenuCategory (MenuCategory category) {
        form = new MenuCategoryForm(category);
        form.addSaveListener(this::saveMenuCategory);
        form.addCloseListener(e -> closeEditor());

        setMenuCategoryData();
    }

    private void saveMenuCategory(MenuCategoryForm.SaveEvent event) {
        menuCategory.saveCategory(event.getMenuCategory());
        setMenuCategoryData();
        closeEditor();
    }

    private void closeEditor() {
        form.setMenuCategory(null);
        form.setVisible(false);
    }

    private void configureGrid() {
        grid.addClassNames("menu-category-grid");
        grid.setWidthFull();
        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        getThemeList().add("spacing-xs");

        Grid.Column<MenuCategory> orderNo = grid.addColumn(MenuCategory::getOrderNo).setSortProperty();
        orderNo.setVisible(false);
        grid.addColumn(MenuCategory::getName);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, category) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_SMALL);
                    button.getElement().setAttribute("aria-label", "Edit category");
                    button.addClickListener(e ->
                            createMenuCategory(category));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
            new ComponentRenderer<>(Button::new, (button, category) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                        ButtonVariant.LUMO_ERROR,
                        ButtonVariant.LUMO_SMALL);
                button.getElement().setAttribute("aria-label", "Delete category");
                button.setIcon(new Icon(VaadinIcon.TRASH));
                button.addClickListener(e -> deleteMenuCategory(category));
            })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

        GridSortOrder<MenuCategory> order = new GridSortOrder<>(orderNo, SortDirection.ASCENDING);
        grid.sort(List.of(order));
    }

    public void setMenuCategoryData() {
        grid.setItems(menuCategory.findAllCategories());
        grid.asSingleSelect().addValueChangeListener(event ->
                items.setMenuItemData(event.getValue()));
    }

    private void deleteMenuCategory (MenuCategory category) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle(
                String.format("Delete item \"%s\"?",
                        category.getName()));
        dialog.add("Are you sure you want to delete this category permanently?");

        Button deleteButton = new Button("Delete", (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);
        deleteButton.addClickListener(e -> {
            List<MenuItems> items = menuItem.findItemsByCategory(category, null);
            items.forEach(menuItem::deleteItem);
            menuCategory.deleteCategory(category);

            setMenuCategoryData();
        });

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }
}