package com.iwaitless.application.views.utility;

import com.iwaitless.application.persistence.entity.*;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationTypes;
import com.iwaitless.application.views.list.NotificationsView;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;

import java.text.SimpleDateFormat;

public class Renderers {
    public static Renderer<OrderDetails> createOrderDetailRenderer() {
        return LitRenderer.<OrderDetails>of(
                        "<vaadin-horizontal-layout class=\"item-container\" style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.name} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.comment}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", e -> e.getItem().getName())
                .withProperty("comment", e -> {
                    String comment = e.getComment();
                    if (comment != null && !comment.isEmpty())
                        comment = "Comment: " + comment;

                    return comment;
                });
    }

    public static Renderer<Orders> createOrderRenderer() {
        return LitRenderer.<Orders> of(
                        "<vaadin-horizontal-layout class=\"item-container\" style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.name} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.table}" + "    </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.assignee}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", e -> "Order #" + e.getOrderNo())
                .withProperty("table", e -> "for table: " + e.getTableRelation().getTable().getTableNo())
                .withProperty("assignee", e -> {
                    if (e.getTableRelation().getEmployee().getEmployeeId() != 999999L) {
                        String firstName = e.getTableRelation().getEmployee().getFirstName();
                        String lastName = e.getTableRelation().getEmployee().getLastName();

                        if (firstName != null && !firstName.isEmpty())
                            return "(assigned to " + firstName + " " + lastName + ")";
                    }

                    return "(table not assigned)";
                });
    }

    public static Renderer<Orders> createOrderDateRenderer() {
        return LitRenderer.<Orders> of(
                        "<vaadin-horizontal-layout class=\"item-container\" style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.orderedOn} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.time}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("orderedOn", e -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    return formatter.format(e.getOrderedOn());
                })
                .withProperty("time", e -> "(" + NotificationsView.getTimeAgo(e.getOrderedOn()) + ")");
    }


    public static Renderer<MenuItemsOrder> createItemOrderRenderer() {
        return LitRenderer.<MenuItemsOrder>of(
                        "<vaadin-horizontal-layout class=\"item-container\" style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.name} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.description}" + "    </span>"
                                + "    <span> ${item.price} ${item.currency} </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", MenuItemsOrder::getName)
                .withProperty("description", MenuItemsOrder::getDescription)
                .withProperty("price", e -> String.format("%.2f", e.getPrice()))
                .withProperty("currency", e -> e.getCurrency().getCurrencyCode());
    }

    public static Renderer<MenuItems> createItemRenderer() {
        return LitRenderer.<MenuItems> of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.name} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.description}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", MenuItems::getName)
                .withProperty("description", MenuItems::getDescription);
    }

    public static Renderer<Notifications> createNotificationRenderer() {
        return LitRenderer.<Notifications> of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.type} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.table}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("type", notification -> {
                    NotificationTypes notificationTypes = notification.getType();
                    if (notificationTypes.getId().equals("1")) {
                        return notificationTypes.getName() + " #" + notification.getOrder().getOrderNo();
                    }

                    return notificationTypes.getName();
                })
                .withProperty("table", notification -> "for table " + notification.getTable().getTableNo());
    }

    public static Renderer<Staff> createEmployeeRenderer() {
        return LitRenderer.<Staff> of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<vaadin-avatar name=\"${item.name}\" alt=\"User avatar\"></vaadin-avatar>"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.name} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.role}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("name", employee -> employee.getFirstName() + " " + employee.getLastName())
                .withProperty("role", employee -> employee.getRole().getName());
    }
}
