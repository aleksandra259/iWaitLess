package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.OrderDetails;
import com.iwaitless.application.persistence.entity.Orders;
import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.application.services.OrderDetailsService;
import com.iwaitless.application.services.OrderStatusService;
import com.iwaitless.application.services.OrdersService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
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
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDetailsPopup extends FormLayout {

    Dialog dialog = new Dialog();
    Button close = new Button(new Icon(VaadinIcon.CLOSE));

    private final Orders order;
    private final OrdersService service;
    private final OrderStatusService statusService;

    private final Grid<Orders> grid;
    private final List<OrderDetails> orderDetails;

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

        getStyle().set("width", "30rem").set("max-width", "100%");
        dialog.setWidth("30rem");
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.START, LumoUtility.BorderRadius.NONE,
                LumoUtility.Margin.NONE);

        dialog.setHeaderTitle("Order #" + order.getOrderNo());
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
        close.addClickListener(event -> {
            fireEvent(new OrderDetailsPopup.CloseEvent(this));
            dialog.close();
        });

        String orderStatus = order.getStatus().getId();

        switch (orderStatus) {
            case "1" -> {  // Cancel order, Approve order
                Button cancelOrderButton = new Button("Cancel order");
                Button approveOrderButton = new Button("Approve order");

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
                Span statusSpan = new Span("Order status: Finalized");
                statusSpan.setClassName("order-status-finalized");
                dialog.getFooter().add(statusSpan);
            }
            case "-1" -> {  // Order Status: Cancelled
                Span statusSpan = new Span("Order status: Cancelled");
                statusSpan.setClassName("order-status-cancelled");
                dialog.getFooter().add(statusSpan);
            }
            default -> {  // Order status: dropdown menu to update status, Cancel order
                Button cancelOrderButton = new Button("Cancel order");
                cancelOrderButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                cancelOrderButton.addClickListener(event -> {
                    order.setStatus(statusService.findStatusById("-1"));
                    service.saveOrder(order);
                    dialog.close();
                    grid.getDataProvider().refreshAll();
                });

                Span status = new Span("Order status: ");
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
        Paragraph description = new Paragraph("for table: "
                + order.getTableRelation().getTable().getTableNo()
                + " (" + order.getTableRelation().getTable().getDescription() + ")");
        description.addClassNames(LumoUtility.FontSize.LARGE);

        Grid<OrderDetails> gridDetails = new Grid<>(OrderDetails.class, false);
        gridDetails.setItems(orderDetails);

        gridDetails.setWidthFull();
        gridDetails.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        gridDetails.addColumn(createOrderDetailRenderer()).setHeader("Item");
        gridDetails.addColumn(e -> "Qty: " + e.getQuantity()).setHeader("Quantity")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        gridDetails.addColumn(e -> String.format("%.2f", e.getItem().getPrice())
                        + " " + e.getItem().getCurrency()).setHeader("Price")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);


        Paragraph totalAmountLabel = new Paragraph("Total price: " + getTotalPrice());
        totalAmountLabel.setClassName("total-amount-text");

        dialog.add(description, gridDetails, totalAmountLabel);
    }

    private static Renderer<OrderDetails> createOrderDetailRenderer() {
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

    // Events
    public static abstract class MenuItemPopupEvent extends ComponentEvent<OrderDetailsPopup> {
        private final MenuItems item;

        protected MenuItemPopupEvent(OrderDetailsPopup source, MenuItems item) {
            super(source, false);
            this.item = item;
        }

        public MenuItems getMenuItem() {
            return item;
        }
    }

    public static class CloseEvent extends MenuItemPopupEvent {
        CloseEvent(OrderDetailsPopup source) {
            super(source, null);
        }
    }

    public void addCloseListener(ComponentEventListener<OrderDetailsPopup.CloseEvent> listener) {
        addListener(OrderDetailsPopup.CloseEvent.class, listener);
    }
}


