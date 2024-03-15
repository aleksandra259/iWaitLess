package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import java.util.Comparator;
import java.util.List;

import static com.iwaitless.application.views.MenuPreviewView.createAnchorLink;

public class MenuLoadView extends VerticalLayout
        implements HasComponents, HasStyle {

    MenuCategoryService menuCategory;
    RestaurantTableService restaurantTable;
    MenuItemService menuItem;
    List<MenuCategory> categorySorted;
    RestaurantTable table;

    TextField searchField = new TextField();
    VerticalLayout menuLayout = new VerticalLayout();

    public MenuLoadView(MenuCategoryService menuCategory,
                        MenuItemService menuItem,
                        RestaurantTableService restaurantTable,
                        RestaurantTable table) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;
        this.table = table;

        HorizontalLayout menuBar = new HorizontalLayout();
        H2 header = new H2("Menu");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.SMALL, FontSize.XXLARGE);

        menuLayout.setWidthFull();
        menuLayout.addClassName("fixed-menu-bar");
        menuLayout.addClassNames(LumoUtility.Background.CONTRAST_5);

        categorySorted = menuCategory.findAllCategories();
        categorySorted.sort(Comparator.comparing(MenuCategory::getOrderNo));
        categorySorted.forEach(category -> {
            if (!menuItem.findItemsByCategory(category, searchField.getValue()).isEmpty()) {
                String anchorLink = createAnchorLink(category.getId());
                Button button = new Button(category.getName(), event ->
                        UI.getCurrent().getPage().executeJs("window.location.hash = $0", anchorLink));
                button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

                menuBar.add(button);
            }
        });

        SplitLayout splitLayout = new SplitLayout(searchField, menuBar);
        menuLayout.add(header, splitLayout);
        setMenuData();

        searchField.setPlaceholder("Search by name or ingredient...");
        searchField.addValueChangeListener(event -> setMenuData());
    }

    private void setMenuData () {
        removeAll();

        MenuItemsPreviewView itemsPreview
                = new MenuItemsPreviewView(menuItem, categorySorted,
                                           searchField.getValue(),
                                           table);
        itemsPreview.addClassName("menu-categories-page");
        add(menuLayout, itemsPreview);
    }
}
