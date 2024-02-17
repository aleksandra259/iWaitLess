package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItem;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.Random;

public class MenuCategoriesView extends VerticalLayout {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    MenuItemsView items;
    H2 categoriesHeader = new H2();
    Button newCategory = new Button();
    Grid<MenuCategory> grid = new Grid<>(MenuCategory.class, false);
    MenuCategory currentCategory = new MenuCategory();
    Editor<MenuCategory> editor = grid.getEditor();

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
        newCategory.addClickListener(e -> {
            MenuCategory category = new MenuCategory();
            String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            Random random = new Random();

            category.setId(candidateChars.charAt(random.nextInt(candidateChars.length()))
                + candidateChars.charAt(random.nextInt(candidateChars.length()))
                + candidateChars.charAt(random.nextInt(candidateChars.length()))
                + candidateChars.charAt(random.nextInt(candidateChars.length()))
                + "_" + candidateChars.charAt(random.nextInt(candidateChars.length())));
            category.setName(" ");
            currentCategory = category;

            menuCategory.saveCategory(category);
            setMenuCategoryData();

//            if (editor.isOpen())
//                editor.cancel();
//            grid.getEditor().editItem(category);
        });

        categoriesHeader.setText("Categories");
        categoriesHeader.setWidth("max-content");
        categoriesHeader.setMaxWidth("150px");

        setMenuCategoryData();

        HorizontalLayout categories = new HorizontalLayout(categoriesHeader, newCategory);
        add(categories, grid);
    }

    private void configureGrid() {
        grid.addClassNames("menu-category-grid");
        grid.setSizeFull();

        Grid.Column<MenuCategory> name = grid
                .addColumn(MenuCategory::getName);
        Grid.Column<MenuCategory> editColumn = grid.addComponentColumn(category -> {
            Button editButton = new Button();
            editButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_TERTIARY);
            editButton.setIcon(new Icon(VaadinIcon.EDIT));
            editButton.getElement().setAttribute("aria-label", "Edit category");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(category);
                currentCategory = category;
            });
            return editButton;
        }).setWidth("0.5em");
        Binder<MenuCategory> binder = new Binder<>(MenuCategory.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField nameField = new TextField();
        nameField.setWidthFull();
        binder.forField(nameField)
                .asRequired("Category name cannot be null")
                .bind(MenuCategory::getName, MenuCategory::setName);
        name.setEditorComponent(nameField);

        Button saveButton = new Button("Save", e -> {
            currentCategory.setName(nameField.getValue());
            menuCategory.saveCategory(currentCategory);
            editor.save();
        });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, category) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.getElement().setAttribute("aria-label", "Delete category");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                    button.addClickListener(e ->
                            deleteMenuCategory(category));
                })).setWidth("0.5em");

        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        getThemeList().clear();
        getThemeList().add("spacing-s");
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
            List<MenuItem> items = menuItem.findItemsByCategory(category);
            items.forEach(item -> menuItem.deleteItem(item));
            menuCategory.deleteCategory(category);

            setMenuCategoryData();
        });

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }
}