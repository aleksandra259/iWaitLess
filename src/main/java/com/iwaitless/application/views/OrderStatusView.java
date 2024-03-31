package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.Orders;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.MenuCategoryService;
import com.iwaitless.application.services.MenuItemService;
import com.iwaitless.application.services.OrdersService;
import com.iwaitless.application.services.RestaurantTableService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Timer;

@PageTitle("iWaitLess | Order Status")
@Route("order-status")
@AnonymousAllowed
public class OrderStatusView extends VerticalLayout {
    private final MenuCategoryService menuCategory;
    private final MenuItemService menuItem;
    private final RestaurantTableService restaurantTable;
    private final OrdersService ordersService;
    String tableNo;
    RestaurantTable table;
    VaadinSession vaadinSession = VaadinSession.getCurrent();

    VerticalLayout orderStatusLayout = new VerticalLayout();
    HashMap<ProgressBar, Integer> progressBarList = new HashMap<>();

    private Timer timer;
    private Registration timerRegistration;
    Long orderNo;
    Orders order;

    public OrderStatusView(MenuCategoryService menuCategory,
                           MenuItemService menuItem,
                           RestaurantTableService restaurantTable,
                           OrdersService ordersService) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;
        this.ordersService = ordersService;

        orderNo = (Long)vaadinSession.getAttribute("orderNo");
        tableNo = (String)vaadinSession.getAttribute("tableNo");
        if (tableNo != null)
            table = restaurantTable.findTableByTableNo(tableNo);

        H2 header = new H2("Order #" + orderNo + " tracker");
        orderStatusLayout.add(createOrderStatusComponent("Order Received"));
        orderStatusLayout.add(createOrderStatusComponent("Order Accepted"));
        orderStatusLayout.add(createOrderStatusComponent("Preparing"));
        orderStatusLayout.add(createOrderStatusComponent("Quality Check"));
        orderStatusLayout.add(createOrderStatusComponent("Ready to be served"));

        notifyProgressBarUpdate();

        add(header, orderStatusLayout, new MenuPreviewLayout(menuCategory, menuItem, table));
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
            order = ordersService.findOrderByOrderNo(orderNo);
            if (order != null) {
                status = order.getStatus().getId();
            }
        }

        updateProgressBarValues(status);
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
