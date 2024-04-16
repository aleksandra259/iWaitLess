package com.iwaitless.application.views.list;

import com.iwaitless.application.persistence.entity.Orders;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.application.services.*;
import com.iwaitless.application.views.MainLayout;
import com.iwaitless.application.views.forms.OrderDetailsPopup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Orders")
@Route(value = "orders", layout = MainLayout.class)
@AnonymousAllowed
public class OrdersView extends VerticalLayout {
    Grid<Orders> grid = new Grid<>(Orders.class, false);
    CheckboxGroup<String> statusFilter = new CheckboxGroup<>();
    CheckboxGroup<String> tableFilter = new CheckboxGroup<>();
    Checkbox myOrders = new Checkbox();

    private final OrdersService service;
    private final OrderStatusService statusService;
    private final OrderDetailsService detailsService;
    private final RestaurantTableService tableService;
    private final TableEmployeeRelationService tableEmployeeService;
    private final StaffService staffService;

    public OrdersView(OrdersService service,
                      OrderDetailsService detailsService,
                      OrderStatusService statusService,
                      RestaurantTableService tableService,
                      TableEmployeeRelationService tableEmployeeService,
                      StaffService staffService) {
        this.service = service;
        this.detailsService = detailsService;
        this.statusService = statusService;
        this.tableService = tableService;
        this.tableEmployeeService = tableEmployeeService;
        this.staffService = staffService;

        addClassName("list-order-view");
        setSizeFull();
        configureGrid();
        setOrderData();

        Div gridContainer = new Div();
        gridContainer.addClassNames("order-grid");
        gridContainer.setWidthFull();
        gridContainer.getStyle().set("overflow", "auto");
        gridContainer.add(grid);

        add(getToolbar(), gridContainer);
    }

    private void configureGrid() {
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

        grid.addColumn(createOrderRenderer()).setHeader("Order");
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

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, order) -> {
                    button.setText("Check order details");
                    button.addClickListener(e ->
                        new OrderDetailsPopup(order, service, detailsService, statusService, grid));
                })).setTextAlign(ColumnTextAlign.END);
    }

    private static Renderer<Orders> createOrderRenderer() {
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
                .withProperty("table", e -> "for table: " + e.getTableRelationId().getTableId().getTableNo())
                .withProperty("assignee", e -> {
                    if (e.getTableRelationId().getEmployeeId().getEmployeeId() != 999999L) {
                        String firstName = e.getTableRelationId().getEmployeeId().getFirstName();
                        String lastName = e.getTableRelationId().getEmployeeId().getLastName();

                        if (firstName != null && !firstName.isEmpty())
                            return "(assigned to " + firstName + " " + lastName + ")";
                    }

                    return "(table not assigned)";
                });
    }

    private VerticalLayout getToolbar() {
        List<String> statusNames = statusService.findAllStatuses()
                .stream()
                .map(OrderStatus::getName)
                .collect(Collectors.toList());
        List<String> tableNames = tableService.findAllTables(null)
                .stream()
                .map(RestaurantTable::getTableNo)
                .collect(Collectors.toList());

        statusFilter.setLabel("Status");
        statusFilter.setItems(statusNames);
        statusFilter.addValueChangeListener(e -> setOrderData());

        tableFilter.setLabel("Table");
        tableFilter.setItems(tableNames);
        tableFilter.addValueChangeListener(e -> setOrderData());

        myOrders.setLabel("Filter my orders");
        myOrders.addValueChangeListener(e -> setOrderData());

        HorizontalLayout buttonLayout = getHorizontalLayout();

        VerticalLayout toolbar = new VerticalLayout(myOrders, statusFilter, buttonLayout);
        toolbar.addClassName("toolbar");
        toolbar.setSpacing(false);
        toolbar.setPadding(false);

        return toolbar;
    }

    private HorizontalLayout getHorizontalLayout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> myTables = tableEmployeeService
                .findAllAssignedTables(staffService
                        .findEmployeeByUsername(authentication.getName()))
                .stream()
                .map(RestaurantTable::getTableNo)
                .collect(Collectors.toSet());

        Button myTablesButton = new Button("Filter my tables");
        myTablesButton.addClickListener(e -> {
            tableFilter.select(myTables);
        });

        if (myTables.isEmpty())
            myTablesButton.setEnabled(false);

        HorizontalLayout buttonLayout = new HorizontalLayout
                (tableFilter, myTablesButton);
        buttonLayout.setAlignItems(Alignment.BASELINE);
        return buttonLayout;
    }

    private void setOrderData() {
        List<Orders> filteredOrders = service.findAllOrders();

        // Apply status filter
        Set<String> selectedStatuses = statusFilter.getSelectedItems();
        if (!selectedStatuses.isEmpty()) {
            filteredOrders = filteredOrders.stream()
                    .filter(order -> selectedStatuses.contains(order.getStatus().getName()))
                    .collect(Collectors.toList());
        }

        // Apply order filter
        if (myOrders.getValue()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            filteredOrders = filteredOrders.stream()
                    .filter(order -> username.equals(order.getTableRelationId().getEmployeeId().getUsername()))
                    .collect(Collectors.toList());
        }

        // Apply table filter
        Set<String> selectedTables = tableFilter.getSelectedItems();
        if (!selectedTables.isEmpty()) {
            filteredOrders = filteredOrders.stream()
                    .filter(order -> selectedTables.contains(order.getTableRelationId().getTableId().getTableNo()))
                    .collect(Collectors.toList());
        }


        grid.setItems(filteredOrders);
    }
}
