package com.iwaitless.views;

import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.entity.nomenclatures.MenuCategory;
import com.iwaitless.services.MenuCategoryService;
import com.iwaitless.services.MenuItemService;
import com.iwaitless.views.forms.CallWaiterPopup;
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

@AnonymousAllowed
public class MenuPreviewLayout extends AppLayout {

    private final MenuCategoryService menuCategory;
    private final MenuItemService menuItem;
    private final RestaurantTable table;

    VerticalLayout categories = new VerticalLayout();
    HorizontalLayout navigation = new HorizontalLayout();


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
        navigation.setClassName("main-drawer");
        navigation.setWidthFull();

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
        H3 orderHeader = new H3("Поръчки");
        H3 categoriesHeader = new H3("Категории");
        H3 personalizeHeader = new H3("Персонализиране на менюто");
        orderHeader.getStyle().set("margin-top", "30px");
        categoriesHeader.getStyle().set("margin-top", "30px");
        personalizeHeader.getStyle().set("margin-top", "30px");
        categories.getStyle().set("padding-top", "20px");
        categories.setSpacing(false);
        categories.setHeightFull();
        categories.setClassName("main-drawer");

        RouterLink orderStatus = new RouterLink("Състояние на поръчката", OrderStatusView.class);
        orderStatus.addClassName("order-status-link-button");
        RouterLink mostPopular = new RouterLink("Популярни", PopularMenuItemsView.class);
        mostPopular.addClassName("order-status-link-button");
        RouterLink personalize = new RouterLink("Персонализиране", PersonalizedMenuView.class);
        personalize.addClassName("order-status-link-button");

        categories.add(orderHeader, orderStatus, personalizeHeader,
                personalize, categoriesHeader, mostPopular);

        List<MenuCategory> categorySorted = menuCategory.findAllCategories();
        categorySorted.sort(Comparator.comparing(MenuCategory::getOrderNo));
        categorySorted.forEach(category -> {
            if (!menuItem.findAvailableItemsByCategory
                    (category, "").isEmpty()) {
                String anchorLink = MenuPreviewView.createAnchorLink(category.getId());
                Button button = new Button(category.getName(), event ->
                        UI.getCurrent().getPage().executeJs("window.location.hash = $0", anchorLink));
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

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
