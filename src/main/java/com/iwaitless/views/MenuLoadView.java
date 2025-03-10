package com.iwaitless.views;

import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.services.MenuCategoryService;
import com.iwaitless.services.MenuItemService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Comparator;
import java.util.List;

public class MenuLoadView extends VerticalLayout
        implements HasComponents, HasStyle {

    private final MenuItemService menuItem;
    private final RestaurantTable table;
    private final boolean tableExists;
    private final boolean showSearch;
    private final boolean showVegetarian;
    private final boolean showVegan;

    TextField searchField = new TextField();
    VerticalLayout menuLayout = new VerticalLayout();
    List<MenuCategory> categorySorted;


    public MenuLoadView(MenuCategoryService menuCategory,
                        MenuItemService menuItem,
                        RestaurantTable table,
                        boolean search,
                        boolean isVegetarian,
                        boolean isVegan) {
        this.menuItem = menuItem;
        this.table = table;
        this.showSearch = search;
        this.showVegetarian = isVegetarian;
        this.showVegan = isVegan;

        menuLayout.setWidthFull();

        tableExists = (table != null && table.getTableId() != null);
        HorizontalLayout menuBar = new HorizontalLayout();

        searchField.setPlaceholder("Търсене по име или съставка...");
        searchField.addValueChangeListener(event -> setMenuData());
        searchField.setWidthFull();
        if (showSearch && tableExists) {
            Image logo = new Image("images/logo.png", "iWaitLess Logo");
            logo.setWidth("40%");
            menuLayout.add(logo, searchField);
        }

        categorySorted = menuCategory.findAllCategories();
        categorySorted.sort(Comparator.comparing(MenuCategory::getOrderNo));
        if (!tableExists) {
            categorySorted.forEach(category -> {
                if (!menuItem.findAvailableItemsByCategory
                        (category, searchField.getValue()).isEmpty()) {
                    String anchorLink = MenuPreviewView.createAnchorLink(category.getId());
                    Button button = new Button(category.getName(), event ->
                            UI.getCurrent().getPage().executeJs("window.location.hash = $0", anchorLink));
                    button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

                    menuBar.add(button);
                }
            });
        }

        if (!tableExists) {
            Image logo = new Image("images/logo.png", "iWaitLess Logo");
            logo.setWidth("15%");

            SplitLayout splitLayout = new SplitLayout(searchField, menuBar);
            menuLayout.add(logo, splitLayout);
        }

        setMenuData();
    }

    private void setMenuData () {
        removeAll();

        MenuItemsPreviewView itemsPreview
                = new MenuItemsPreviewView(menuItem, categorySorted,
                                           searchField.getValue(),
                                           table, showVegetarian, showVegan);
        if (!tableExists) {
            menuLayout.addClassName("fixed-menu-bar");
            add(new H1("^"), new H1("^"), menuLayout);
        } if (showSearch) {
            menuLayout.addClassName("fixed-menu-bar");
            add(new H1("^"),menuLayout);
        }

        add(itemsPreview);
    }
}
