package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.views.forms.CallWaiterPopup;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iwaitless.application.views.MenuPreviewView.createAnchorLink;

@AnonymousAllowed
public class MenuPreviewLayout extends AppLayout {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    VerticalLayout categories = new VerticalLayout();
    HorizontalLayout navigation = new HorizontalLayout();
    RestaurantTable table;

    public MenuPreviewLayout(MenuCategoryService menuCategory,
                             MenuItemService menuItem,
                             RestaurantTable table) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.table = table;

        addToNavbar(true, getNavigation());
        setSubMenuData();
    }

    private HorizontalLayout getNavigation() {
        DrawerToggle subMenu = new DrawerToggle();

        navigation.addClassNames(LumoUtility.Width.FULL,
                LumoUtility.JustifyContent.EVENLY,
                LumoUtility.AlignSelf.STRETCH);
        navigation.getStyle().set("position", "fixed");
        navigation.getStyle().set("bottom", "0");
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
                link.setRoute(MenuCatalogueView.class, "table=" + table.getTableNo());
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

    private void setSubMenuData () {
        H3 orderHeader = new H3("Orders");
        H3 header = new H3("Categories");
        header.getStyle().set("margin-top", "20px");
        categories.getStyle().set("padding-top", "20px");
        categories.setSpacing(false);

        RouterLink orderStatus = new RouterLink("Check Order Status", OrderStatusView.class);
        orderStatus.addClassName("order-status-link-button");
        RouterLink mostPopular = new RouterLink("Popular", PopularMenuItemsView.class);
        mostPopular.addClassName("order-status-link-button");

        categories.add(orderHeader, orderStatus, header, mostPopular);

        List<MenuCategory> categorySorted = menuCategory.findAllCategories();
        categorySorted.sort(Comparator.comparing(MenuCategory::getOrderNo));
        categorySorted.forEach(category -> {
            if (!menuItem.findAvailableItemsByCategory
                    (category, "").isEmpty()) {
                String anchorLink = createAnchorLink(category.getId());
                Button button = new Button(category.getName(), event ->
                        UI.getCurrent().getPage().executeJs("window.location.hash = $0", anchorLink));
                button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
                button.getStyle().set("color", "black");

                categories.add(button);
            }
        });

        addToDrawer(categories);
    }

    public static Map<String, String> getParameters (String parameter) {
        Map<String, String> paramMap = new HashMap<>();
        String[] keyValuePairs = parameter.split("&");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2)
                paramMap.put(keyValue[0], keyValue[1]);

        }

        return paramMap;
    }
    public static String getTableNo (String parameter) {
        Map<String, String> paramMap = getParameters (parameter);
        VaadinSession.getCurrent().setAttribute("tableNo", paramMap.get("table"));

        return paramMap.get("table");
    }
}
