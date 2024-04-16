package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.Orders;
import com.iwaitless.application.services.OrderDetailsService;
import com.iwaitless.application.services.OrdersService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

@PageTitle("Home Page")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomePageView extends VerticalLayout {

    private final OrderDetailsService detailsService;

    private final OrdersService service;

    Grid<Orders> grid = new Grid<>(Orders.class, false);

    public HomePageView(OrdersService service,
                        OrderDetailsService detailsService) {
        this.detailsService = detailsService;
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createOrderGrid();

        // Intro
        HorizontalLayout dashboard = new HorizontalLayout(
                new VerticalLayout(
                        new H2("Welcome to iWaitLess Employee Dashboard"),
                        new Paragraph("This dashboard provides an overview of created "
                                + "orders and statistics for the last 7 days.")),
                new VerticalLayout(
                        new H2("Order Statistics"),
                        createStatisticsLayout()));

        // Visualization of orders
        add(dashboard, new H2("Orders"), grid);
    }

    private void createOrderGrid() {
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.addClassName("orders-grid");
        grid.setPartNameGenerator(order -> {
            if ("-1".equals(order.getStatus().getId()))
                return "cancelled";
            if ("6".equals(order.getStatus().getId()))
                return "done";
            return null;
        });

        grid.addColumn(Orders::getOrderNo).setHeader("Order");
        grid.addColumn(order -> detailsService.getPriceByOrder(order.getOrderNo()))
                .setHeader("Total price")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(order -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    return formatter.format(order.getOrderedOn());
                })
                .setHeader("Ordered On").setSortable(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Orders::getOrderedOn).reversed())
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(order -> order.getStatus().getName())
                .setHeader("Status").setSortable(true)
                .setAutoWidth(true).setFlexGrow(0);


        List<Orders> filteredOrders = service.findAllOrders();
        grid.setItems(filteredOrders);
    }

    private Component createStatisticsLayout() {
        // You can customize this layout to display various statistics
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Span("Total Revenue: $1000"));
        layout.add(new Span("Orders Today: 10"));
        layout.add(new Span("Orders This Week: 50"));
        return layout;
    }

}