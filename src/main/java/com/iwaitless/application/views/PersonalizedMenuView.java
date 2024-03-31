package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("menu")
@AnonymousAllowed
public class PersonalizedMenuView extends VerticalLayout {

    RestaurantTable table = new RestaurantTable();
    MenuLoadView menuLoad;
    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    RestaurantTableService restaurantTable;
    VaadinSession vaadinSession = VaadinSession.getCurrent();


    Div content = new Div();
    Checkbox filterAll = new Checkbox("All");
    Checkbox filterVegetarian = new Checkbox("Vegetarian");
    Checkbox filterVegan = new Checkbox("Vegan");

    public PersonalizedMenuView(MenuCategoryService menuCategory,
                                MenuItemService menuItem,
                                RestaurantTableService restaurantTable) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;

        String tableNo = (String)vaadinSession.getAttribute("tableNo");
        if (tableNo != null) {
            table = restaurantTable.findTableByTableNo(tableNo);
        }

        vaadinSession.setAttribute("consistFilter", "");
        vaadinSession.setAttribute("notConsistFilter", "");

        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setWidthFull();
        H1 title = new H1("iWaitLess | Menu");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-xs) var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs) var(--lumo-space-xs)");
        menuLayout.add(title);
        menuLayout.addClassName("fixed-menu-bar");
        menuLayout.addClassNames(LumoUtility.Background.CONTRAST_5);


        add(menuLayout, createPersonalizeComponent(), content,
                new MenuPreviewLayout(menuCategory, menuItem, table));
    }

    private VerticalLayout createTextLayout(String text) {
        VerticalLayout textLayout = new VerticalLayout();
        HorizontalLayout consistsLayout = new HorizontalLayout();
        TextField consist = new TextField(text);
        Span consistElements = new Span();
        Button consistButton = new Button("", VaadinIcon.SEARCH.create());

        consistButton.addClickListener(e -> {
            if (consistElements.getText() != null && !consistElements.getText().isEmpty())
                consistElements.add(", ");

            consistElements.add(consist.getValue());
            consist.setValue("");

            if (text.equals("Consist")) {
                vaadinSession.setAttribute("consistFilter", consistElements.getText());
            } else if (text.equals("Does not consist")) {
                vaadinSession.setAttribute("notConsistFilter", consistElements.getText());
            }
            filterMenuItems();
        });
        consistsLayout.add(consist, consistButton);
        consistsLayout.setAlignItems(Alignment.BASELINE);
        consistsLayout.setWidthFull();
        textLayout.setSpacing(false);
        consistElements.getStyle().set("color", "grey");
        consistElements.getStyle().set("font-style", "italic");

        textLayout.add(consistsLayout, consistElements);
        return textLayout;
    }

    private Component createPersonalizeComponent() {
        Accordion accordion = new Accordion();
        accordion.addClassName("popular-items-page");
        accordion.setWidthFull();

        FormLayout personalizeFormLayout =  new FormLayout();
        VerticalLayout detailsPanel = new VerticalLayout();
        AccordionPanel customDetailsPanel = accordion.add("Personalize",
                personalizeFormLayout);
        customDetailsPanel.addThemeVariants(DetailsVariant.FILLED);
        customDetailsPanel.addClassName("personalize-menu-layout");
        detailsPanel.setSpacing(false);

        // Kitchen Type filter
        filterAll.addValueChangeListener(event -> {
            if (filterAll.getValue()) {
                filterVegetarian.setValue(true);
                filterVegan.setValue(true);
            } else {
                filterVegetarian.setValue(false);
                filterVegan.setValue(false);
            }
            filterMenuItems();
        });
        filterVegetarian.addValueChangeListener(event -> filterMenuItems());
        filterVegan.addValueChangeListener(event -> filterMenuItems());

        VerticalLayout kitchenTypeLayout = new VerticalLayout(
                new Span("Kitchen Type"),
                new HorizontalLayout(filterAll, filterVegetarian, filterVegan)
        );

        detailsPanel.add(kitchenTypeLayout,
                createTextLayout("Consist"),
                createTextLayout("Does not consist"));
        customDetailsPanel.add(detailsPanel);

        return accordion;
    }

    private void filterMenuItems() {
        if (menuLoad != null) {
            content.removeAll();
            menuLoad.removeAll();
        }

        if (filterAll.getValue()) {
            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable,
                table, false, false, false);
        } else if (filterVegan.getValue()) {
            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable,
                    table, false, true, true);
        } else if (filterVegetarian.getValue()) {
            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable,
                    table, false, true, false);
        }

        if (menuLoad != null)
            content.add(menuLoad);
    }
}

