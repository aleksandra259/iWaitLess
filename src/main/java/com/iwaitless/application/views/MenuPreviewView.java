package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Map;

@Route("menu-preview")
@PageTitle("Menu Preview")
@AnonymousAllowed
public class MenuPreviewView extends VerticalLayout
        implements HasComponents, HasStyle, HasUrlParameter<String> {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    RestaurantTableService restaurantTable;
    MenuLoadView menuLoad;

    VaadinSession vaadinSession = VaadinSession.getCurrent();

    public MenuPreviewView(MenuCategoryService menuCategory,
                           MenuItemService menuItem,
                           RestaurantTableService restaurantTable) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;
    }

    public static String createAnchorLink(String category) {
        return "category#" + category;
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();
        Map<String, List<String>> parametersMap =
                queryParameters.getParameters();
        removeAll();

        List<String> values = parametersMap.get("table");
        if (values != null) {
            RestaurantTable table = restaurantTable.findTableByTableNo(values.get(0));
            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable, table, false);

            vaadinSession.setAttribute("tableNo", values.get(0));
        } else {
            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable, null, false);
        }

        add(menuLoad);
    }
}
