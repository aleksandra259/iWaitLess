package com.iwaitless.application.views.list;


import com.iwaitless.application.persistence.entity.Notifications;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.application.services.*;
import com.iwaitless.application.views.forms.NotificationPopup;
import com.iwaitless.application.views.forms.OrderDetailsPopup;
import com.iwaitless.application.views.utility.Renderers;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.provider.SortDirection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class NotificationsView extends Dialog {

    private final NotificationsService service;
    private final Staff staff;

    private final OrdersService orderService;
    private final OrderDetailsService orderDetailService;
    private final OrderStatusService statusService;

    public NotificationsView(NotificationsService service,
                             StaffService staffService,
                             OrdersService orderService,
                             OrderDetailsService orderDetailService,
                             OrderStatusService statusService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.staff = staffService.findEmployeeByUsername(authentication.getName());
        this.service = service;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.statusService = statusService;

        setWidth("20rem");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        loadNotifications();
    }

    private void loadNotifications() {
        Grid<Notifications> grid = new Grid<>(Notifications.class, false);
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.addClassName("notifications-grid");
        grid.setPartNameGenerator(notification -> {
            if ("R".equals(notification.getStatus().getId()))
                return "read";
            if ("U".equals(notification.getStatus().getId()))
                return "not-read";
            return null;
        });

        grid.addColumn(Renderers.createNotificationRenderer())
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(n -> getTimeAgo(n.getRegistrationDate()))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);
        grid.setItems(service.findNotificationsByEmployee(staff));
        add(grid);

        Grid.Column<Notifications> date = grid.addColumn(Notifications::getRegistrationDate);
        date.setVisible(false);

        GridSortOrder<Notifications> order = new GridSortOrder<>(date, SortDirection.DESCENDING);
        grid.sort(List.of(order));

        grid.addSelectionListener(event -> {
            if (!event.getAllSelectedItems().isEmpty()) {
                Notifications notification = event.getFirstSelectedItem().get();
                if ("1".equals(notification.getType().getId())) {
                    new OrderDetailsPopup(notification.getOrder(),
                            orderService,
                            orderDetailService,
                            statusService,
                            null);
                } else {
                    new NotificationPopup(notification);
                }

                NotificationStatus notificationStatus = new NotificationStatus();
                notificationStatus.setId("R");
                notification.setStatus(notificationStatus);

                service.saveNotification(notification);
                grid.getDataProvider().refreshAll();
                close();
            }
        });

    }

    public static String getTimeAgo(Timestamp notificationTimestamp) {
        // Convert Timestamp to Instant and then to LocalDateTime
        LocalDateTime notificationDateTime = notificationTimestamp
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime currentTimestamp = LocalDateTime.now();
        Duration duration = Duration.between(notificationDateTime, currentTimestamp);

        if (duration.toDays() >= 365) {
            long years = duration.toDays() / 365;
            return years + (years == 1 ? " year ago" : " years ago");
        }

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " sec ago";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " min ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hr ago";
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (seconds < 2419200) {
            long weeks = seconds / 604800;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        } else {
            long months = seconds / 2419200;
            return months + (months == 1 ? " month ago" : " months ago");
        }
    }
}
