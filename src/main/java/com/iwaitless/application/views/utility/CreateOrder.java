package com.iwaitless.application.views.utility;

import com.iwaitless.application.persistence.entity.*;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationTypes;
import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.application.services.*;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class CreateOrder {
    private final TableEmployeeRelationService tableEmployeeRelation;
    private final OrdersService ordersService;
    private final OrderDetailsService orderDetailsService;
    private final NotificationsService notificationsService;
    private final MenuItemService menuItem;

    private final RestaurantTable table;
    private final List<MenuItemsOrder> cartItems;

    VaadinSession vaadinSession = VaadinSession.getCurrent();


    public CreateOrder (OrdersService ordersService,
                        OrderDetailsService orderDetailsService,
                        NotificationsService notificationsService,
                        MenuItemService menuItem,
                        TableEmployeeRelationService tableEmployeeRelation,
                        RestaurantTable table) {
        this.ordersService = ordersService;
        this.orderDetailsService = orderDetailsService;
        this.notificationsService = notificationsService;
        this.menuItem = menuItem;
        this.tableEmployeeRelation = tableEmployeeRelation;
        this.table = table;

        // Retrieve the cart items from the session
        cartItems = (List<MenuItemsOrder>) vaadinSession.getAttribute("cartItems");

        saveOrder();
    }

    private void saveOrder () {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setId("1");

        Orders order = new Orders();
        order.setOrderedOn(Timestamp.from(Instant.now()));
        order.setStatus(orderStatus);
        order.setTableRelation(tableEmployeeRelation.findTableRelationByTable(table));

        ordersService.saveOrder(order);
        vaadinSession.setAttribute("orderNo", order.getOrderNo());

        saveOrderDetails(order);
        saveNotification(order);
    }

    private void saveOrderDetails (Orders order) {
        OrderDetails orderDetails;

        if (cartItems != null && !cartItems.isEmpty()) {
            for (MenuItemsOrder item : cartItems) {
                orderDetails = new OrderDetails();
                orderDetails.setOrder(order);
                orderDetails.setItem(menuItem.findItemById(item.getItemId()));
                orderDetails.setQuantity(item.getQuantity());
                orderDetails.setComment(item.getComment());

                orderDetailsService.saveOrderDetail(orderDetails);
            }

            cartItems.clear();
        }
    }

    private void saveNotification (Orders order) {
        NotificationTypes notificationType = new NotificationTypes();
        notificationType.setId("1");
        NotificationStatus notificationStatus = new NotificationStatus();
        notificationStatus.setId("U");

        Notifications notification = new Notifications();
        notification.setEmployee(order.getTableRelation().getEmployee());
        notification.setType(notificationType);
        notification.setStatus(notificationStatus);
        notification.setOrder(order);
        notification.setTable(table);
        notification.setRegistrationDate(Timestamp.from(Instant.now()));

        notificationsService.saveNotification(notification);
    }
}
