package com.iwaitless.services;

import com.iwaitless.domain.NotificationsMockData;
import com.iwaitless.domain.StaffMockData;
import com.iwaitless.persistence.entity.Notifications;
import com.iwaitless.persistence.entity.Staff;
import com.iwaitless.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.persistence.repository.NotificationsRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsServiceTests {
    @Mock
    private NotificationsRepository notificationsRepository;
    @InjectMocks
    private NotificationsService notificationsService;

    private final Staff employee = StaffMockData.initStaff();
    private final Notifications readNotification = NotificationsMockData.initNotification();
    private final Notifications unreadNotification = NotificationsMockData.initNotification();


    @BeforeEach
    void setUp() {
        NotificationStatus readStatus = new NotificationStatus();
        readStatus.setId("R");
        readNotification.setStatus(readStatus);

        NotificationStatus unreadStatus = new NotificationStatus();
        readStatus.setId("U");
        unreadNotification.setStatus(unreadStatus);
    }

    @Test
    void testFindNotificationsByEmployee() {
        when(notificationsRepository.findAll()).thenReturn(List.of(readNotification, unreadNotification));

        List<Notifications> result = notificationsService.findNotificationsByEmployee(employee);
        assertEquals(2, result.size());
        assertTrue(result.contains(readNotification));
        assertTrue(result.contains(unreadNotification));
    }

    @Test
    void testFindNotificationsByEmployee_EmployeeNull() {
        assertThrows(IllegalArgumentException.class, () -> notificationsService.findNotificationsByEmployee(null));
    }

    @Test
    void testCountUnreadNotificationsByEmployee() {
        when(notificationsRepository.findAll()).thenReturn(List.of(readNotification, unreadNotification));

        int unreadCount = notificationsService.countUnreadNotificationsByEmployee(employee);
        assertEquals(1, unreadCount);
    }

    @Test
    void testCountUnreadNotificationsByEmployee_EmployeeNull() {
        assertThrows(IllegalArgumentException.class,
                () -> notificationsService.countUnreadNotificationsByEmployee(null));
    }

    @Test
    void testSaveNotification() {
        notificationsService.saveNotification(unreadNotification);
        verify(notificationsRepository, times(1)).save(unreadNotification);
    }

    @Test
    void testSaveNotification_NullNotification() {
        assertThrows(IllegalArgumentException.class, () -> notificationsService.saveNotification(null));
    }
}

