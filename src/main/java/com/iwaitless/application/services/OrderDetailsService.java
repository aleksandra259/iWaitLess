package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.OrderDetails;
import com.iwaitless.application.persistence.repository.OrderDetailsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    this.getOrderDetailsByOrderNo(orderDetails.getOrderNo().getOrderNo());

            for (OrderDetails orderItem : orderItems) {
                MenuItems orderedItem = orderItem.getItemId();
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
                    this.getOrderDetailsByOrderNo(orderDetails.getOrderNo().getOrderNo());

            for (OrderDetails orderItem : orderItems)
                menuItems.add(orderItem.getItemId());
        }

        return menuItems;
    }

    public void saveOrderDetail(OrderDetails orderDetail) {
        orderDetailsRepository.save(orderDetail);
    }

    public List<OrderDetails> getOrderDetailsByOrderNo(Long orderNo) {
        return orderDetailsRepository.findByOrderNo_OrderNo(orderNo);
    }

    public void deleteOrderDetail(Long detailId) {
        orderDetailsRepository.deleteById(detailId);
    }

    public int getNumberOfItemsInOrder(Long orderNo) {
        return orderDetailsRepository.countByOrderNoOrderNo(orderNo);
    }
}

