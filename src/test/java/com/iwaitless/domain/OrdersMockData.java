package com.iwaitless.domain;

import com.iwaitless.persistence.entity.OrderDetails;
import com.iwaitless.persistence.entity.Orders;
import com.iwaitless.persistence.entity.TableEmployeeRelation;
import com.iwaitless.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.persistence.entity.nomenclatures.PaymentWay;

import java.sql.Timestamp;
import java.time.LocalDate;
public class OrdersMockData {
    private OrdersMockData() {}

    public static Orders initOrder() {
        Orders order = new Orders();
        order.setOrderNo(1L);
        order.setOrderedOn(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        order.setStatus(new OrderStatus());
        order.setPaymentWay(new PaymentWay());
        order.setTableRelation(new TableEmployeeRelation());

        return order;
    }

    public static OrderDetails initOrderDetails() {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setDetailId(1L);
        orderDetails.setItem(MenuItemMockData.initAvailableMenuItem());
        orderDetails.setQuantity(2);
        orderDetails.setOrder(initOrder());

        return orderDetails;
    }

    public static OrderStatus initOrderStatus() {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setId("1");
        orderStatus.setName("Pending");

        return orderStatus;
    }
}
