package com.iwaitless.domain;
import com.iwaitless.persistence.entity.Notifications;
import com.iwaitless.persistence.entity.Orders;
import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.entity.nomenclatures.NotificationTypes;

import java.sql.Timestamp;
import java.time.LocalDate;

public class NotificationsMockData {
    private NotificationsMockData() {}

    public static Notifications initNotification() {
        Notifications notification = new Notifications();
        notification.setNotificationId(1L);
        notification.setEmployee(StaffMockData.initStaff());
        notification.setType(new NotificationTypes());
        notification.setTable(new RestaurantTable());
        notification.setOrder(new Orders());
        notification.setRegistrationDate(Timestamp.valueOf(LocalDate.now().atStartOfDay()));

        return notification;
    }

}
