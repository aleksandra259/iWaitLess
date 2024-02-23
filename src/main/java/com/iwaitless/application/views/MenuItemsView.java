package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItem;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuItemService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Currency;

public class MenuItemsView extends VerticalLayout {
    Grid<MenuItem> grid = new Grid<>(MenuItem.class, false);
    BeanValidationBinder<MenuItem> binder = new BeanValidationBinder<>(MenuItem.class);
    Button newFood = new Button();
    H2 foodListHeader = new H2();

    MenuItemService menuItem;
    MenuCategory category;


    public MenuItemsView(MenuItemService menuItem) {
        this.menuItem = menuItem;
        this.category = new MenuCategory();

        addClassName("items-view");
        setSizeFull();

        newFood.setText("New");
        newFood.setWidth("min-content");
        newFood.setMaxWidth("100px");
        newFood.addClickListener(e -> {
                    if (category.getId() != null) {
                        MenuItem item = new MenuItem();
                        item.setCategory(category);
                        createMenuItem(item);
                    }
                    else {
                        Notification notification = new Notification();
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

                        Div text = new Div(new Text("To be able to create item you should select category first."));

                        Button closeButton = new Button(new Icon("lumo", "cross"));
                        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                        closeButton.setAriaLabel("Close");
                        closeButton.addClickListener(event -> notification.close());

                        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
                        layout.setAlignItems(Alignment.CENTER);

                        notification.add(layout);
                        notification.open();
                    }
                });

        foodListHeader.setText("Food list");
        foodListHeader.setWidth("max-content");
        foodListHeader.setMaxWidth("150px");

        configureGrid();

        HorizontalLayout categories = new HorizontalLayout(foodListHeader, newFood);
        add(categories, grid);
    }

    public void setMenuItemData(MenuCategory menuCategory) {
        this.category = menuCategory;

        if (category != null)
            grid.setItems(menuItem.findItemsByCategory(category));
        else {
            MenuCategory dummy = new MenuCategory();
            dummy.setId("-1");
            grid.setItems(menuItem.findItemsByCategory(dummy));
        }
    }

    private void configureGrid() {
        grid.addClassNames("menu-item-grid");
        grid.setSizeFull();

        grid.addColumn(MenuItem::getName);
        grid.addColumn(MenuItem::getDescription);
        grid.addColumn(item -> item.getPrice() + " " + item.getCurrency());
        grid.addColumn(item -> item.getSize() + " gram");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, item) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_TERTIARY);
                    button.getElement().setAttribute("aria-label", "Edit item");
                    button.addClickListener(e ->
                            createMenuItem(item));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                })).setWidth("0.5em");
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, item) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.getElement().setAttribute("aria-label", "Delete item");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                    button.addClickListener(e ->
                        deleteMenuItem(item));
                })).setWidth("0.5em");

        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    private void deleteMenuItem (MenuItem item) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle(
                String.format("Delete item \"%s\"?",
                              item.getName()));
        dialog.add("Are you sure you want to delete this item permanently?");

        Button deleteButton = new Button("Delete", (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);
        deleteButton.addClickListener(e -> {
            menuItem.deleteItem(item);
            setMenuItemData(category);
        });

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    private void createMenuItem (MenuItem item) {
        Dialog dialog = new Dialog();

        String header = item.getName();
        if (header.isEmpty())
            dialog.setHeaderTitle("New item");
        else
            dialog.setHeaderTitle(item.getCategory().getName() + " - " + header);

        FormLayout dialogLayout = createDialogLayout(item);
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog, item);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        add(dialog);
        dialog.open();

        menuItem.saveItem(item);
    }

    private FormLayout createDialogLayout(MenuItem item) {
        TextField itemNameField = new TextField("Name");
        TextArea description = new TextArea("Description");
        NumberField size = new NumberField("Size");
        size.setSuffixComponent(new Span("grams"));
        NumberField timeToProcess = new NumberField("Time to process");
        timeToProcess.setSuffixComponent(new Span("minutes"));
        UploadImage image = new UploadImage(item);

        NumberField price = new NumberField();
        Select<Currency> currency = new Select<>();
        currency.setItems(Currency.getInstance("BGN"),
                Currency.getInstance("EUR"));
        currency.getElement().executeJs(
                "this.focusElement.setAttribute('title', 'Currency')");

        HorizontalLayout priceAndCurrency = new HorizontalLayout(price, currency);
        priceAndCurrency.getThemeList().add("spacing-s");

        HorizontalLayout sizeAndTime = new HorizontalLayout(size, timeToProcess);
        sizeAndTime.getThemeList().add("spacing-s");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        formLayout.add(itemNameField, description, sizeAndTime, priceAndCurrency);
        formLayout.addFormItem(priceAndCurrency, "Price");
        formLayout.addFormItem(image, "Image");
        formLayout.getStyle().set("width", "25rem").set("max-width", "100%");

        binder.bind(itemNameField, MenuItem::getName, MenuItem::setName);
        binder.bind(description, MenuItem::getDescription, MenuItem::setDescription);
        binder.bind(size, MenuItem::getSize, MenuItem::setSize);
        binder.bind(timeToProcess, MenuItem::getTimeToProcess, MenuItem::setTimeToProcess);
        binder.bind(price, MenuItem::getPrice, MenuItem::setPrice);
        binder.forField(currency).bind(MenuItem::getCurrency, MenuItem::setCurrency);
        setItem(item);

        return formLayout;
    }

    private Button createSaveButton(Dialog dialog,
                                    MenuItem item) {
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (binder.isValid()) {
                menuItem.saveItem(item);
                setMenuItemData(category);
                dialog.close();
            }
        });

        return saveButton;
    }

    public void setItem(MenuItem item) {
        binder.setBean(item);
    }
}