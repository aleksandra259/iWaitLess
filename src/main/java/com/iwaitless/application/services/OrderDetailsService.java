package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.OrderDetails;
import com.iwaitless.application.persistence.repository.OrderDetailsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;


    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
    }

    public List<OrderDetails> findAll() {
        return orderDetailsRepository.findAll();
    }

    public List<MenuItems> findCommonOrderedItems(MenuItems item) {
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        List<MenuItems> commonItems = new ArrayList<>();

        for (OrderDetails orderDetails : orderDetailsList) {
            List<OrderDetails> orderItems =
                    this.findOrderDetailsByOrderNo(orderDetails.getOrder().getOrderNo());

            for (OrderDetails orderItem : orderItems) {
                MenuItems orderedItem = orderItem.getItem();
                if (!orderedItem.equals(item) && !commonItems.contains(orderedItem)) {
                    commonItems.add(orderedItem);
                }
            }
        }

        return commonItems;
    }

    public List<MenuItems> findAllOrderedItems() {
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        List<MenuItems> menuItems = new ArrayList<>();

        for (OrderDetails orderDetails : orderDetailsList) {
            List<OrderDetails> orderItems =
                    this.findOrderDetailsByOrderNo(orderDetails.getOrder().getOrderNo());

            for (OrderDetails orderItem : orderItems)
                menuItems.add(orderItem.getItem());
        }

        return menuItems;
    }

    public String getPriceByOrder (Long orderNo) {
        BigDecimal sum = BigDecimal.ZERO;
        String currency = "EUR";
        List<OrderDetails> orderDetails = orderDetailsRepository.findByOrder_OrderNo(orderNo);

        for (OrderDetails orderDetail : orderDetails) {
            BigDecimal quantity = BigDecimal.valueOf(orderDetail.getQuantity());
            BigDecimal price = BigDecimal.valueOf(orderDetail.getItem().getPrice());
            BigDecimal subtotal = quantity.multiply(price);

            sum = sum.add(subtotal);
            currency = orderDetail.getItem().getCurrency().getCurrencyCode();
        }
        
        return String.format("%.2f", sum) + " " + currency;
    }

    public List<OrderDetails> findOrderDetailsByOrderNo(Long orderNo) {
        return orderDetailsRepository.findByOrder_OrderNo(orderNo);
    }

    public void saveOrderDetail(OrderDetails orderDetail) {
        orderDetailsRepository.save(orderDetail);
    }
}

