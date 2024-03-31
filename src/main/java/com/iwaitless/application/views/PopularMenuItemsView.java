package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.OrderDetailsService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route("menu-preview-popular")
@PageTitle("Menu Preview")
@AnonymousAllowed
public class PopularMenuItemsView extends VerticalLayout implements HasComponents, HasStyle{
    MenuItemService menuItem;
    OrderDetailsService orderDetailsService;

    RestaurantTable table = new RestaurantTable();
    VaadinSession vaadinSession = VaadinSession.getCurrent();
    VerticalLayout popularItems = new VerticalLayout();

    public PopularMenuItemsView(MenuCategoryService menuCategory,
                                MenuItemService menuItem,
                                RestaurantTableService restaurantTable,
                                OrderDetailsService orderDetailsService) {
        this.menuItem = menuItem;
        this.orderDetailsService = orderDetailsService;

        String tableNo = (String)vaadinSession.getAttribute("tableNo");
        if (tableNo != null) {
            table = restaurantTable.findTableByTableNo(tableNo);
        }

        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setWidthFull();
        H1 title = new H1("iWaitLess | Popular items");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                    .set("margin", "var(--lumo-space-xs) var(--lumo-space-xs)")
                    .set("padding", "var(--lumo-space-xs) var(--lumo-space-xs)");
        menuLayout.add(title);
        menuLayout.addClassName("fixed-menu-bar");
        menuLayout.addClassNames(LumoUtility.Background.CONTRAST_5);

        popularItems.addClassNames("image-gallery-view", "popular-items-page");
        popularItems.addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Horizontal.AUTO,
                LumoUtility.Margin.Bottom.LARGE, LumoUtility.Padding.Horizontal.SMALL);

        add(menuLayout, popularItems, new MenuPreviewLayout(menuCategory, menuItem, table));
        popularItemsData();
    }

    private void popularItemsData() {
        popularItems.removeAll();

        List<MenuItems> items = menuItem.findAvailableItems();
        if (items != null && !items.isEmpty()) {
            OrderedList imageContainer = new OrderedList();
            imageContainer.addClassNames(LumoUtility.Gap.SMALL, LumoUtility.Display.GRID,
                    LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);

            List<MenuItems> orderedItems = new ArrayList<>(orderDetailsService.findAllOrderedItems());
            if (!orderedItems.isEmpty()) {
                Set<MenuItems> uniqueItems = new HashSet<>();
                orderedItems.forEach(item -> {
                    if (item.isAvailable() && uniqueItems.stream()
                            .noneMatch(existingItem -> existingItem.getItemId().equals(item.getItemId()))) {
                        uniqueItems.add(item);
                    }
                });

                uniqueItems.stream()
                        .limit(20)
                        .forEach(item -> {
                            imageContainer.add(new MenuItemViewCard(item, table));
                        });

                popularItems.add(imageContainer);
            }
        }
    }
}
