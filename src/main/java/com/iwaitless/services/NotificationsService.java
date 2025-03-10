package com.iwaitless.services;

import com.iwaitless.persistence.entity.Notifications;
import com.iwaitless.persistence.entity.Staff;
import com.iwaitless.persistence.repository.NotificationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;

    public NotificationsService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    public List<Notifications> findNotificationsByEmployee(Staff employee) {
        if (employee == null || employee.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee information is required.");
        }

        return notificationsRepository
                .findAll()
                .stream()
                .filter(notification ->
                        notification.getEmployee().getEmployeeId().equals(employee.getEmployeeId())
                                || notification.getEmployee().getEmployeeId() == 999999L)
                .collect(Collectors.toList());
    }

    public int countUnreadNotificationsByEmployee(Staff employee) {
        if (employee == null || employee.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee information is required.");
        }

        return (int) findNotificationsByEmployee(employee)
                .stream()
                .filter(e -> "U".equals(e.getStatus().getId()))
                .count();
    }

    public void saveNotification(Notifications notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Cannot save a null notification.");
        }
        notificationsRepository.save(notification);
    }
}