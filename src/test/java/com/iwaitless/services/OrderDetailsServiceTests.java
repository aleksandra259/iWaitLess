package com.iwaitless.services;

import com.iwaitless.domain.OrdersMockData;
import com.iwaitless.persistence.entity.OrderDetails;
import com.iwaitless.persistence.repository.OrderDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDetailsServiceTests {
    @Mock
    private OrderDetailsRepository orderDetailsRepository;
    @InjectMocks
    private OrderDetailsService orderDetailsService;

    private final OrderDetails orderDetails = OrdersMockData.initOrderDetails();
    private final List<OrderDetails> orderDetailsList = List.of(orderDetails, orderDetails);


    @BeforeEach
    void setUp() {
        lenient().when(orderDetailsRepository.findAll()).thenReturn(orderDetailsList);
        lenient().when(orderDetailsService.findOrderDetailsByOrderNo(1L)).thenReturn(orderDetailsList);
    }

    @Test
    void testFindCommonOrderedItems_ItemNull() {
        assertThrows(IllegalArgumentException.class, () -> orderDetailsService.findCommonOrderedItems(null));
    }

    @Test
    void testGetPriceByOrder() {
        when(orderDetailsRepository.findByOrder_OrderNo(1L)).thenReturn(orderDetailsList);

        String price = orderDetailsService.getPriceByOrder(1L);
        assertEquals("100.00 EUR", price);
    }

    @Test
    void testGetPriceByOrder_OrderNoNull() {
        assertThrows(IllegalArgumentException.class, () -> orderDetailsService.getPriceByOrder(null));
    }

    @Test
    void testFindOrderDetailsByOrderNo() {
        when(orderDetailsRepository.findByOrder_OrderNo(1L)).thenReturn(orderDetailsList);

        List<OrderDetails> result = orderDetailsService.findOrderDetailsByOrderNo(1L);
        assertEquals(2, result.size());
        assertTrue(result.contains(orderDetails));
    }

    @Test
    void testFindOrderDetailsByOrderNo_OrderNoNull() {
        assertThrows(IllegalArgumentException.class, () -> orderDetailsService.findOrderDetailsByOrderNo(null));
    }

    @Test
    void testSaveOrderDetail() {
        orderDetailsService.saveOrderDetail(orderDetails);
        verify(orderDetailsRepository, times(1)).save(orderDetails);
    }

    @Test
    void testSaveOrderDetail_NullOrderDetail() {
        assertThrows(IllegalArgumentException.class, () -> orderDetailsService.saveOrderDetail(null));
    }
}

