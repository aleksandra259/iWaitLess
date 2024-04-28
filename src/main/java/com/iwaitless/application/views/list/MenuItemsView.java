package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.views.forms.MenuItemEditPopup;
import com.iwaitless.application.views.utility.Renderers;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MenuItemsView extends VerticalLayout {

    Grid<MenuItems> grid = new Grid<>(MenuItems.class, false);
    Button newFood = new Button("Нов");
    H2 foodListHeader = new H2("Артикули");

    private final MenuItemService menuItem;

    MenuCategory category;
    MenuItemEditPopup form;


    public MenuItemsView(MenuItemService menuItem) {
        this.menuItem = menuItem;
        this.category = new MenuCategory();

        addClassName("items-view");
        setSizeFull();
        configureGrid();

        newFood.setWidth("min-content");
        newFood.setMaxWidth("100px");
        newFood.addClickListener(e -> {
                    if (category != null && category.getId() != null) {
                        MenuItems item = new MenuItems();
                        item.setCategory(category);
                        createMenuItem(item);
                    }
                    else {
                        Notification notification = new Notification(
                                "За да създадете атрибут, първо трябва да изберете категория.",
                                3000);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.open();
                    }
                });

        add(new HorizontalLayout(foodListHeader, newFood), grid);
    }

    public void setMenuItemData(MenuCategory menuCategory) {
        this.category = menuCategory;

        if (category != null)
            grid.setItems(menuItem.findItemsByCategory(category, null));
        else {
            MenuCategory dummy = new MenuCategory();
            dummy.setId("-1");
            grid.setItems(menuItem.findItemsByCategory(dummy, null));
        }
    }

    private void configureGrid() {
        grid.addClassNames("menu-item-grid");
        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_NO_BORDER);

        grid.addComponentColumn(item -> {
            Image image = MenuItemsView.returnImage(item);
            image.setWidth(125, Unit.PIXELS);
            image.setHeight(80, Unit.PIXELS);

            return image;
        }).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Renderers.createItemRenderer());
        grid.addColumn(item -> String.format("%.2f", item.getPrice()) + " " + item.getCurrency());
        grid.addColumn(item -> item.getSize() + " гр.");
        grid.addColumn(item -> item.isAvailable() ? "Наличен" : "Не е наличен");

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, item) -> {
                    button.getElement().setAttribute("aria-label", "Редактиране на артикул");
                    button.addClickListener(e -> createMenuItem(item));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                    button.addClassName("edit-button");
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, item) -> {
                    button.getElement().setAttribute("aria-label", "Изтрий артикул");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                    button.addClassName("delete-button");
                    button.addClickListener(e -> deleteMenuItem(item));
                })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
    }

    private void deleteMenuItem (MenuItems item) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle(
                String.format("Изтрий на артикул \"%s\"?",
                              item.getName()));
        dialog.add("Сигурни ли сте, че искате да изтриете този артикул завинаги?");

        Button deleteButton = new Button("Изтрий", (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);
        deleteButton.addClickListener(e -> {
            menuItem.deleteItem(item);
            setMenuItemData(category);
        });

        Button cancelButton = new Button("Отказ", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    private void createMenuItem (MenuItems item) {
        form = new MenuItemEditPopup(item);
        form.addSaveListener(this::saveMenuItem);
        form.addCloseListener(e -> closeEditor());

        setMenuItemData(category);
    }

    private void saveMenuItem(MenuItemEditPopup.SaveEvent event) {
        menuItem.saveItem(event.getMenuItem());
        setMenuItemData(category);
        closeEditor();
    }

    private void closeEditor() {
        form.setItem(null);
        form.setVisible(false);
    }

    public static Image returnImage (MenuItems item) {
        File folder = new File("D:/iwaitless/menu-items/"
                + item.getCategory().getId()
                + "/"
                + item.getItemId());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File currentFile : listOfFiles) {
                if (currentFile.isFile()) {
                    return getImage(item, currentFile);
                }
            }
        }

        return new Image("images/picture-not-available.png", "");
    }

    private static Image getImage(MenuItems item, File currentFile) {
        StreamResource imageResource = new StreamResource(currentFile.getName(), () -> {
            try {
                return new FileInputStream("D:\\iwaitless\\menu-items\\" + item.getCategory().getId()
                        + "\\"
                        + item.getItemId()
                        + "\\"
                        + currentFile.getName());
            } catch (final FileNotFoundException e) {
                return null;
            }
        });

        return new Image(imageResource, currentFile.getName());
    }
}