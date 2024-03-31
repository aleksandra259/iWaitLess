package com.iwaitless.application.views.utility;

import com.iwaitless.application.persistence.entity.MenuItemsOrder;
import com.iwaitless.application.persistence.entity.OrderDetails;
import com.iwaitless.application.persistence.entity.Orders;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.*;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class CreateOrder {
    private final TableEmployeeRelationService tableEmployeeRelation;
    private final OrderStatusService orderStatusService;
    private final RestaurantTable table;
    private final List<MenuItemsOrder> cartItems;

    private final OrdersService ordersService;
    private final OrderDetailsService orderDetailsService;
    private final MenuItemService menuItem;

    VaadinSession vaadinSession = VaadinSession.getCurrent();



    public CreateOrder (OrdersService ordersService,
                        OrderDetailsService orderDetailsService,
                        MenuItemService menuItem,
                        TableEmployeeRelationService tableEmployeeRelation,
                        OrderStatusService orderStatusService,
                        RestaurantTable table) {
        this.ordersService = ordersService;
        this.orderDetailsService = orderDetailsService;
        this.menuItem = menuItem;
        this.tableEmployeeRelation = tableEmployeeRelation;
        this.orderStatusService = orderStatusService;
        this.table = table;

        // Retrieve the cart items from the session
        cartItems = (List<MenuItemsOrder>) vaadinSession.getAttribute("cartItems");

        saveOrder();
    }

    private void saveOrder () {
        Orders order = new Orders();
        order.setOrderedOn(Timestamp.from(Instant.now()));
        order.setStatus(orderStatusService.findStatusById("1"));
        order.setTableRelationId(tableEmployeeRelation.findTableRelationByTable(table));

        ordersService.saveOrder(order);
        vaadinSession.setAttribute("orderNo", order.getOrderNo());

        saveOrderDetails(order);
    }

    private void saveOrderDetails (Orders order) {
        OrderDetails orderDetails;

        if (cartItems != null && !cartItems.isEmpty()) {
            for (MenuItemsOrder item : cartItems) {
                orderDetails = new OrderDetails();
                orderDetails.setOrderNo(order);
                orderDetails.setItemId(menuItem.findItemById(item.getItemId()));
                orderDetails.setQuantity(item.getQuantity());
                orderDetails.setComment(item.getComment());
                orderDetails.setStatus(orderStatusService.findStatusById("C"));

                orderDetailsService.saveOrderDetail(orderDetails);
            }

            cartItems.clear();
        }
    }
}
