package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.Notifications;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.repository.NotificationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;

    public NotificationsService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    public List<Notifications> findAllNotifications() {
        return notificationsRepository.findAll();
    }

    public List<Notifications> findNotificationsByEmployee(Staff employee) {
        return notificationsRepository
                .findAll()
                .stream()
                .filter(notification ->
                        notification.getEmployee().getEmployeeId().equals(employee.getEmployeeId()))
                .collect(Collectors.toList());
    }
    public Notifications findNotificationById(Long id) {
        return notificationsRepository
                .findById(id)
                .orElse(null);
    }

    public void saveNotification(Notifications notification) {
        if (notification == null) {
            System.err.println("Notification save failed");
            return;
        }

        notificationsRepository.save(notification);
    }
}