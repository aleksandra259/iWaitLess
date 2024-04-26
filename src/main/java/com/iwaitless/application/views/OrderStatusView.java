package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.Orders;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.OrdersService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@PageTitle("iWaitLess | Order Status")
@Route("order-status")
@AnonymousAllowed
public class OrderStatusView extends VerticalLayout {

    private final OrdersService ordersService;
    private final Long orderNo;

    HashMap<ProgressBar, Integer> progressBarList = new HashMap<>();


    public OrderStatusView(MenuCategoryService menuCategory,
                           MenuItemService menuItem,
                           RestaurantTableService restaurantTable,
                           OrdersService ordersService) {
        this.ordersService = ordersService;

        VaadinSession vaadinSession = VaadinSession.getCurrent();
        orderNo = (Long) vaadinSession.getAttribute("orderNo");

        String tableNo = (String) vaadinSession.getAttribute("tableNo");
        RestaurantTable table = new RestaurantTable();
        if (tableNo != null)
            table = restaurantTable.findTableByTableNo(tableNo);

        if (orderNo != null) {
            H2 header = new H2("Order #" + orderNo + " tracker");
            header.addClassName("tables-grid");

            VerticalLayout orderStatusLayout = new VerticalLayout();
            orderStatusLayout.add(createOrderStatusComponent("Order Received"));
            orderStatusLayout.add(createOrderStatusComponent("Order Accepted"));
            orderStatusLayout.add(createOrderStatusComponent("Preparing"));
            orderStatusLayout.add(createOrderStatusComponent("Quality Check"));
            orderStatusLayout.add(createOrderStatusComponent("Ready to be served"));

            notifyProgressBarUpdate();

            add(setMenuLayout(), header, orderStatusLayout,
                new MenuPreviewLayout(menuCategory, menuItem, table));
        } else {
            H3 header = new H3("No orders found");
            Span emptyOrder = new Span("Looks like you have not made any order yet. "
                    + "Add something to the cart and finalize your order to see its status.");
            emptyOrder.getStyle().set("font-size", "18px");
            emptyOrder.getStyle().set("text-align", "center");

            Image emptyOrderImage = new Image("images/no-order-made.png", "No data available");
            emptyOrderImage.setWidth("100%");

            add(setMenuLayout(),
                new VerticalLayout(new H1("^"), header, emptyOrderImage, emptyOrder),
                new MenuPreviewLayout(menuCategory, menuItem, table));
        }
    }

    private Div createOrderStatusComponent(String status) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        progressBar.setHeight("25px");
        progressBar.setValue(0);

        NativeLabel progressBarLabelText = new NativeLabel(status);
        progressBarLabelText.setId("pblabel");
        progressBar.getElement().setAttribute("aria-labelledby", "pblabel");

        Div orderStatusComponent = new Div(progressBarLabelText, progressBar);
        orderStatusComponent.setWidthFull();

        progressBarList.put(progressBar, getOrderStatusValue(status));

        return orderStatusComponent;
    }

    private void updateProgressBarValues(String status) {
        progressBarList.forEach((progressBar, state) -> {
            if (state <= Integer.parseInt(status)) {
                progressBar.setValue(1.0);
            } else if (state == Integer.parseInt(status) + 1) {
                progressBar.setIndeterminate(true);
            }
        });
    }

    private void notifyProgressBarUpdate() {
        String status = "0";
        if (orderNo != null) {
            Orders order = ordersService.findOrderByOrderNo(orderNo);
            if (order != null) {
                status = order.getStatus().getId();
            }
        }

        updateProgressBarValues(status);
    }

    private VerticalLayout setMenuLayout () {
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setWidthFull();
        H1 title = new H1("iWaitLess | Order status");
        title.getStyle().set("font-size", "var(--lumo-font-size-xl)")
                .set("margin", "var(--lumo-space-xs) var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs) var(--lumo-space-xs)");
        menuLayout.add(title);
        menuLayout.addClassName("fixed-menu-bar");
        menuLayout.addClassNames(LumoUtility.Background.CONTRAST_5);

        return menuLayout;
    }

    private int getOrderStatusValue (String status) {
        return switch (status) {
            case "Order Received" -> ProgressBarStatus.ORDER_RECEIVED.getValue();
            case "Order Accepted" -> ProgressBarStatus.ORDER_ACCEPTED.getValue();
            case "Preparing" -> ProgressBarStatus.PREPARING.getValue();
            case "Quality Check" -> ProgressBarStatus.QUALITY_CHECK.getValue();
            case "Ready to be served" -> ProgressBarStatus.DELIVERED.getValue();
            default -> 0;
        };
    }

    @Getter
    @RequiredArgsConstructor
    public enum ProgressBarStatus {
        ORDER_RECEIVED(1),
        ORDER_ACCEPTED(2),
        PREPARING(3),
        QUALITY_CHECK(4),
        DELIVERED(5);

        private final int value;
    }
}
