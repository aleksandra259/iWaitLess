package com.iwaitless.services;

import com.iwaitless.domain.OrdersMockData;
import com.iwaitless.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.persistence.repository.nomenclatures.OrderStatusRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTests {
    @Mock
    private OrderStatusRepository orderStatusRepository;
    @InjectMocks
    private OrderStatusService orderStatusService;

    private final OrderStatus orderStatus = OrdersMockData.initOrderStatus();


    @Test
    void testFindStatusById() {
        when(orderStatusRepository.findById("1")).thenReturn(Optional.of(orderStatus));

        OrderStatus result = orderStatusService.findStatusById("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Pending", result.getName());
    }

    @Test
    void testFindStatusById_IdNull() {
        assertThrows(IllegalArgumentException.class, () -> orderStatusService.findStatusById(null));
    }

    @Test
    void testFindStatusById_IdEmpty() {
        assertThrows(IllegalArgumentException.class, () -> orderStatusService.findStatusById(""));
    }

    @Test
    void testFindStatusByName() {
        when(orderStatusRepository.findAll()).thenReturn(List.of(orderStatus));

        OrderStatus result = orderStatusService.findStatusByName("Pending");
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Pending", result.getName());
    }

    @Test
    void testFindStatusByName_NameNull() {
        assertThrows(IllegalArgumentException.class, () -> orderStatusService.findStatusByName(null));
    }

    @Test
    void testFindStatusByName_NameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> orderStatusService.findStatusByName(""));
    }

    @Test
    void testFindStatusByName_NotFound() {
        when(orderStatusRepository.findAll()).thenReturn(List.of(orderStatus));

        OrderStatus result = orderStatusService.findStatusByName("Completed");
        assertNull(result);
    }

    @Test
    void testFindAllStatuses() {
        when(orderStatusRepository.findAll()).thenReturn(List.of(orderStatus));

        List<OrderStatus> statuses = orderStatusService.findAllStatuses();
        assertNotNull(statuses);
        assertEquals(1, statuses.size());
        assertEquals("Pending", statuses.get(0).getName());
    }
}

