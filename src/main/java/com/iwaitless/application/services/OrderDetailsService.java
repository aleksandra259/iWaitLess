package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.OrderDetails;
import com.iwaitless.application.persistence.repository.OrderDetailsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;

    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
    }
    public OrderDetails saveOrderDetail(OrderDetails orderDetail) {
        return orderDetailsRepository.save(orderDetail);
    }

    public void deleteOrderDetail(Long detailId) {
        orderDetailsRepository.deleteById(detailId);
    }

    public List<OrderDetails> getOrderDetailsByOrderNo(Long orderNo) {
        return orderDetailsRepository.findByOrderNo_OrderNo(orderNo);
    }

    public int getNumberOfItemsInOrder(Long orderNo) {
        return orderDetailsRepository.countByOrderNo_OrderNo(orderNo);
    }
}

