package com.iwaitless.views;

import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.services.MenuCategoryService;
import com.iwaitless.services.MenuItemService;
import com.iwaitless.services.RestaurantTableService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("menu")
@PageTitle("iWaitLess | Персонализиране")
@AnonymousAllowed
public class PersonalizedMenuView extends VerticalLayout {

    private final MenuCategoryService menuCategory;
    private final MenuItemService menuItem;

    RestaurantTable table;
    MenuLoadView menuLoad;
    VaadinSession vaadinSession = VaadinSession.getCurrent();


    Div content = new Div();
    Checkbox filterAll = new Checkbox("Всички");
    Checkbox filterVegetarian = new Checkbox("Вегетарианска");
    Checkbox filterVegan = new Checkbox("Веган");


    public PersonalizedMenuView(MenuCategoryService menuCategory,
                                MenuItemService menuItem,
                                RestaurantTableService restaurantTable) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;

        String tableNo = (String)vaadinSession.getAttribute("tableNo");
        if (tableNo != null) {
            table = restaurantTable.findTableByTableNo(tableNo);
        }

        vaadinSession.setAttribute("consistFilter", "");
        vaadinSession.setAttribute("notConsistFilter", "");

        Image logo = new Image("images/logo.png", "iWaitLess Logo");
        logo.setWidth("50%");

        VerticalLayout menuLayout = new VerticalLayout(logo);
        menuLayout.setWidthFull();
        menuLayout.addClassName("fixed-menu-bar");

        add(menuLayout, new H1("^"), createPersonalizeComponent(), content,
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

            if (text.equals("Съдържа")) {
                vaadinSession.setAttribute("consistFilter", consistElements.getText());
            } else if (text.equals("Не съдържа")) {
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
        accordion.setWidthFull();

        FormLayout personalizeFormLayout = new FormLayout();
        VerticalLayout detailsPanel = new VerticalLayout();
        AccordionPanel customDetailsPanel = accordion.add("Персонализиране",
                personalizeFormLayout);
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
                new Span("Вид кухня"),
                filterAll,
                new HorizontalLayout(filterVegetarian, filterVegan));
        kitchenTypeLayout.setSpacing(false);

        detailsPanel.add(kitchenTypeLayout,
                createTextLayout("Съдържа"),
                createTextLayout("Не съдържа"));
        customDetailsPanel.add(detailsPanel);

        return accordion;
    }

    private void filterMenuItems() {
        if (menuLoad != null) {
            content.removeAll();
            menuLoad.removeAll();
        }

        if (filterAll.getValue()) {
            menuLoad = new MenuLoadView(menuCategory, menuItem,
                table, false, false, false);
        } else if (filterVegan.getValue()) {
            menuLoad = new MenuLoadView(menuCategory, menuItem,
                    table, false, false, true);
        } else if (filterVegetarian.getValue()) {
            menuLoad = new MenuLoadView(menuCategory, menuItem,
                    table, false, true, false);
        }

        if (menuLoad != null)
            content.add(menuLoad);
    }
}

