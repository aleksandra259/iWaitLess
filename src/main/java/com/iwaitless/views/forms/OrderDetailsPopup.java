package com.iwaitless.views.forms;

import com.iwaitless.persistence.entity.OrderDetails;
import com.iwaitless.persistence.entity.Orders;
import com.iwaitless.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.services.OrderDetailsService;
import com.iwaitless.services.OrderStatusService;
import com.iwaitless.services.OrdersService;
import com.iwaitless.views.utility.Renderers;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDetailsPopup extends FormLayout {

    private final Orders order;
    private final OrdersService service;
    private final OrderStatusService statusService;

    private final Grid<Orders> grid;
    private final List<OrderDetails> orderDetails;

    Dialog dialog = new Dialog();
    Button close = new Button(new Icon(VaadinIcon.CLOSE));


    public OrderDetailsPopup(Orders order,
                             OrdersService service,
                             OrderDetailsService orderDetailService,
                             OrderStatusService statusService,
                             Grid<Orders> grid) {
        this.order = order;
        this.service = service;
        this.statusService = statusService;
        this.grid = grid;

        this.orderDetails = orderDetailService.findOrderDetailsByOrderNo(order.getOrderNo());

        addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.START,
                LumoUtility.BorderRadius.NONE, LumoUtility.Margin.NONE);

        dialog.setHeaderTitle("Поръчка #" + order.getOrderNo());
        configureLayout();
        createButtonsLayout();

        HorizontalLayout dialogHeader = new HorizontalLayout(close);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        dialogHeader.setWidthFull();
        dialog.getHeader().add(dialogHeader);
        dialog.setDraggable(true);

        dialog.open();
    }

    private void createButtonsLayout() {
        close.addClickShortcut(Key.ESCAPE);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        close.addClickListener(event -> dialog.close());

        String orderStatus = order.getStatus().getId();

        switch (orderStatus) {
            case "1" -> {  // Cancel order, Approve order
                Button cancelOrderButton = new Button("Откажи поръчка");
                Button approveOrderButton = new Button("Потвърди поръчка");

                approveOrderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                cancelOrderButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                approveOrderButton.addClickListener(event -> {
                    order.setStatus(statusService.findStatusById("2"));
                    service.saveOrder(order);
                    dialog.close();
                    grid.getDataProvider().refreshAll();
                });
                cancelOrderButton.addClickListener(event -> {
                    order.setStatus(statusService.findStatusById("-1"));
                    service.saveOrder(order);
                    dialog.close();
                    grid.getDataProvider().refreshAll();
                });

                HorizontalLayout buttonLayout = new HorizontalLayout(cancelOrderButton, approveOrderButton);
                buttonLayout.getStyle().set("flex-wrap", "wrap");
                buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
                setColspan(buttonLayout, 2);

                dialog.getFooter().add(buttonLayout);
            }
            case "6" -> {  // Order Status: Finalized
                Span statusSpan = new Span("Статус на поръчката: Завършена");
                statusSpan.setClassName("order-status-finalized");
                dialog.getFooter().add(statusSpan);
            }
            case "-1" -> {  // Order Status: Cancelled
                Span statusSpan = new Span("Статус на поръчката: Отказана");
                statusSpan.setClassName("order-status-cancelled");
                dialog.getFooter().add(statusSpan);
            }
            default -> {  // Order status: dropdown menu to update status, Cancel order
                Button cancelOrderButton = new Button("Откажи поръчка");
                cancelOrderButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                cancelOrderButton.addClickListener(event -> {
                    order.setStatus(statusService.findStatusById("-1"));
                    service.saveOrder(order);
                    dialog.close();
                    grid.getDataProvider().refreshAll();
                });

                Span status = new Span("Статус на поръчка: ");
                Select<String> selectStatus = new Select<>();
                selectStatus.setItems(statusService.findAllStatuses()
                        .stream()
                        .map(OrderStatus::getName)
                        .collect(Collectors.toList()));
                selectStatus.setValue(order.getStatus().getName());
                selectStatus.addValueChangeListener(e -> {
                    order.setStatus(statusService.findStatusByName(selectStatus.getValue()));
                    service.saveOrder(order);
                    dialog.close();
                    grid.getDataProvider().refreshAll();
                });
                setColspan(status, 2);

                HorizontalLayout buttonLayout = new HorizontalLayout(status, selectStatus, cancelOrderButton);
                buttonLayout.getStyle().set("flex-wrap", "wrap");
                buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
                buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
                setColspan(buttonLayout, 2);

                dialog.getFooter().add(buttonLayout);
            }
        }
    }

    void configureLayout() {
        Span description = new Span("за маса: "
                + order.getTableRelation().getTable().getTableNo()
                + " (" + order.getTableRelation().getTable().getDescription() + ")");
        description.addClassNames(LumoUtility.FontSize.LARGE);

        Grid<OrderDetails> gridDetails = new Grid<>(OrderDetails.class, false);
        gridDetails.setItems(orderDetails);

        gridDetails.setWidthFull();
        gridDetails.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);

        gridDetails.addColumn(Renderers.createOrderDetailRenderer()).setHeader("Артикул");
        gridDetails.addColumn(e -> "бр: " + e.getQuantity()).setHeader("Количество")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        gridDetails.addColumn(e -> String.format("%.2f", e.getItem().getPrice())
                        + " " + e.getItem().getCurrency()).setHeader("Цена")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);


        Paragraph totalAmountLabel = new Paragraph("Обща стойност: " + getTotalPrice());
        totalAmountLabel.setClassName("total-amount-text");

        dialog.add(description, gridDetails, totalAmountLabel);
    }


    private String getTotalPrice() {
        BigDecimal sum = BigDecimal.ZERO;
        String currency = "EUR";

        if (orderDetails != null)
            for (OrderDetails detail : orderDetails) {
                BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity());
                BigDecimal price = BigDecimal.valueOf(detail.getItem().getPrice());
                BigDecimal subtotal = quantity.multiply(price);

                sum = sum.add(subtotal);
                currency = detail.getItem().getCurrency().getCurrencyCode();
            }

        return String.format("%.2f", sum) + " " + currency;
    }
}


