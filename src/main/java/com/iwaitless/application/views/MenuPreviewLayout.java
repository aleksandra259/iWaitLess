package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.HashMap;
import java.util.Map;

@Route("test")
@AnonymousAllowed
public class MenuPreviewLayout extends AppLayout implements HasUrlParameter<String> {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    RestaurantTableService restaurantTable;
    MenuLoadView menuLoad;

    Div content = new Div();
    VerticalLayout categories = new VerticalLayout();
    HorizontalLayout navigation = new HorizontalLayout();
    RestaurantTable table;

    public MenuPreviewLayout(MenuCategoryService menuCategory,
                             MenuItemService menuItem,
                             RestaurantTableService restaurantTable) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;

        setContent(content);
    }

    private HorizontalLayout getNavigation() {
        DrawerToggle subMenu = new DrawerToggle();
        subMenu.setHeightFull();

        navigation.addClassNames(LumoUtility.Width.FULL,
                LumoUtility.JustifyContent.EVENLY,
                LumoUtility.AlignSelf.STRETCH);
        navigation.setPadding(false);
        navigation.setSpacing(false);
        navigation.add(subMenu,
                createLink(VaadinIcon.HOME, "Home"),
                createLink(VaadinIcon.COMMENT_ELLIPSIS, "Call Waitress"),
                createLink(VaadinIcon.CART, "Orders"));

        return navigation;
    }

    private RouterLink createLink(VaadinIcon icon, String viewName) {
        RouterLink link = new RouterLink();

        switch (viewName) {
            case "Search", "Home":
                if (table != null && table.getTableId() != null) {
                    link = new RouterLink("", MenuPreviewLayout.class,
                            "table=" + table.getTableNo());
                }
                break;
            case "Call Waitress":
//                link.setRoute(viewClass.java);
                break;
            case "Orders":
//                link.setRoute(viewClass.java);
                break;
            default:
        }

        link.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Padding.Horizontal.LARGE,
                LumoUtility.TextColor.SECONDARY);
        link.add(icon.create());
        link.getElement().setAttribute("aria-label", viewName);

        return link;
    }

    @Override
    public void setParameter(BeforeEvent event,
                             String parameter) {
        categories.removeAll();
        navigation.removeAll();
        if (menuLoad != null)
            menuLoad.removeAll();

        Map<String, String> paramMap = new HashMap<>();
        String[] keyValuePairs = parameter.split("&");
        for (String pair : keyValuePairs) {
            // Split the pair based on '=' to extract key and value
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                // Store the key-value pair in the map
                paramMap.put(keyValue[0], keyValue[1]);
            }
        }

        String tableNo = paramMap.get("table");
        if (!tableNo.isEmpty()) {
            table = restaurantTable.findTableByTableNo(tableNo);
            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable, table, categories, true);
            addToDrawer(categories);
        } else {
            System.out.println("table not provided");
        }

        addToNavbar(true, getNavigation());
        content.add(menuLoad);
    }
}
