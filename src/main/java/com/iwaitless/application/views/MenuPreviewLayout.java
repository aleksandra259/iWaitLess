package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.RestaurantTableService;
import com.iwaitless.application.views.forms.CallWaiterPopup;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.HashMap;
import java.util.Map;

@PageTitle("iWaitLess|Menu")
@Route("menu-catalogue")
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
                LumoUtility.AlignSelf.STRETCH,
                LumoUtility.Position.FIXED);
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
            case "Home":
                if (table != null && table.getTableId() != null) {
                    link = new RouterLink("", MenuPreviewLayout.class,
                            "table=" + table.getTableNo());
                }
                break;
            case "Call Waitress":
                link.setRoute(CallWaiterPopup.class);
                break;
            case "Orders":
                link.setRoute(CartView.class);
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

        H3 header = new H3("Categories");
        header.getStyle().set("margin-top", "20px"); // Adjust margin-top as needed
        String tableNo = getTableNo(parameter);
        if (!tableNo.isEmpty()) {
            table = restaurantTable.findTableByTableNo(tableNo);
            categories.add(header);

            menuLoad = new MenuLoadView(menuCategory, menuItem, restaurantTable,
                    table, categories, true);
            categories.getStyle().set("padding-top", "20px");

            addToDrawer(categories);
        } else {
            System.out.println("table not provided");
        }

        addToNavbar(true, getNavigation());
        content.add(menuLoad);
    }

    public static String getTableNo (String parameter) {
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

        return paramMap.get("table");
    }

}
