package com.iwaitless.services;

import com.iwaitless.domain.OrdersMockData;
import com.iwaitless.persistence.entity.Orders;
import com.iwaitless.persistence.repository.OrdersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTests {
    @Mock
    private OrdersRepository ordersRepository;
    @InjectMocks
    private OrdersService ordersService;

    private final Orders order = OrdersMockData.initOrder();


    @Test
    void testFindAllOrders() {
        when(ordersRepository.findAll()).thenReturn(List.of(order));

        List<Orders> result = ordersService.findAllOrders();
        assertEquals(1, result.size());
        assertTrue(result.contains(order));
    }

    @Test
    void testFindOrderByOrderNo() {
        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        Orders result = ordersService.findOrderByOrderNo(1L);
        assertNotNull(result);
        assertEquals(1L, result.getOrderNo());
    }

    @Test
    void testFindOrderByOrderNo_OrderNoNull() {
        assertThrows(IllegalArgumentException.class, () -> ordersService.findOrderByOrderNo(null));
    }

    @Test
    void testFindOrderByOrderNo_OrderNotFound() {
        when(ordersRepository.findById(2L)).thenReturn(Optional.empty());

        Orders result = ordersService.findOrderByOrderNo(2L);
        assertNull(result);
    }

    @Test
    void testSaveOrder_NullOrder() {
        assertThrows(IllegalArgumentException.class, () -> ordersService.saveOrder(null));
    }
}
