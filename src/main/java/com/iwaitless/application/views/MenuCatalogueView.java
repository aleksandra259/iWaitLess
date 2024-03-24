package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("iWaitLess | Menu")
@Route(value = "menu-catalogue")
@PreserveOnRefresh
@AnonymousAllowed
public class MenuCatalogueView extends VerticalLayout implements HasUrlParameter<String> {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    RestaurantTableService restaurantTable;
    MenuLoadView menuLoad;

    Div content = new Div();
    RestaurantTable table;

    public MenuCatalogueView(MenuCategoryService menuCategory,
                             MenuItemService menuItem,
                             RestaurantTableService restaurantTable) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;

        add(content);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (menuLoad != null) {
            content.removeAll();
            menuLoad.removeAll();
        }

        String tableNo = MenuPreviewLayout.getTableNo(parameter);
        if (!tableNo.isEmpty()) {
            table = restaurantTable.findTableByTableNo(tableNo);
            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable,
                        table, true);
            content.add(new MenuPreviewLayout(menuCategory, menuItem, table));
        }


        content.add(menuLoad);
    }
}
