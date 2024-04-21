package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.MenuItemsOrder;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.*;
import com.iwaitless.application.views.utility.CreateOrder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PageTitle("iWaitLess | Cart")
@Route(value = "cart")
@AnonymousAllowed
public class CartView extends VerticalLayout {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    RestaurantTableService restaurantTable;
    OrdersService ordersService;
    OrderDetailsService orderDetailsService;
    TableEmployeeRelationService tableEmployeeRelation;
    OrderStatusService orderStatusService;


    Span totalAmountLabel = new Span();
    Span totalTimeToProcess = new Span();
    H3 header = new H3();
    List<MenuItemsOrder> cartItems;
    RestaurantTable table = new RestaurantTable();
    Button finalizeOrderButton = new Button("Finalize Order");

    Grid<MenuItemsOrder> grid = new Grid<>();
    VerticalLayout suggestedItems = new VerticalLayout();

    VaadinSession vaadinSession = VaadinSession.getCurrent();

    public CartView(MenuCategoryService menuCategory,
                    MenuItemService menuItem,
                    RestaurantTableService restaurantTable,
                    OrdersService ordersService,
                    OrderDetailsService orderDetailsService,
                    TableEmployeeRelationService tableEmployeeRelation,
                    OrderStatusService orderStatusService) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;
        this.ordersService = ordersService;
        this.orderDetailsService = orderDetailsService;
        this.tableEmployeeRelation = tableEmployeeRelation;
        this.orderStatusService = orderStatusService;

        String tableNo = (String)vaadinSession.getAttribute("tableNo");
        if (tableNo != null)
            table = restaurantTable.findTableByTableNo(tableNo);

        configureGrid();
        createButtonsLayout();
        suggestedItemsData();

        header.setText("Cart " + getTotalItemsCount());
        totalAmountLabel.setText("Total amount to pay: " + getTotalPrice());
        totalTimeToProcess.setText("Total time to process: " +  getTotalTimeToProcess());

        totalAmountLabel.addClassName("bold-span");
        totalTimeToProcess.addClassName("bold-span");

        updateContent();
    }

    private String getTotalPrice() {
        BigDecimal sum = BigDecimal.ZERO;
        String currency = "EUR";

        if (cartItems != null)
            for (MenuItemsOrder item : cartItems) {
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                BigDecimal price = BigDecimal.valueOf(item.getPrice());
                BigDecimal subtotal = quantity.multiply(price);

                sum = sum.add(subtotal);
                currency = item.getCurrency().getCurrencyCode();
            }

        return sum + " " + currency;
    }

    private String getTotalTimeToProcess() {
        if (cartItems != null && !cartItems.isEmpty()) {
            double maxTimeToProcess = cartItems.get(0).getTimeToProcess() == null ?
                    0.0d : cartItems.get(0).getTimeToProcess();

            for (MenuItemsOrder item : cartItems)
                if (item.getTimeToProcess() != null && item.getTimeToProcess() > maxTimeToProcess)
                    maxTimeToProcess = item.getTimeToProcess();

            return maxTimeToProcess == 0.0d ?
                    "Undefined" : maxTimeToProcess + " minutes";
        }

        return "Undefined";
    }

    private String getTotalItemsCount() {
        int itemsCount = 0;
        if (cartItems != null && !cartItems.isEmpty())
            for (MenuItemsOrder ignored : cartItems)
                itemsCount += 1;

        return "(" + itemsCount + " elements)";
    }

    private void updateContent() {
        if (cartItems == null || cartItems.isEmpty()) {
            removeAll();
            add(setMenuLayout(), new H1("^"), header, createEmptyStateComponent(),
                    new MenuPreviewLayout(menuCategory, menuItem, table));
        } else {
            removeAll();
            configureGrid();

            add(setMenuLayout(), new H1("^"), header, grid, totalTimeToProcess,
                    totalAmountLabel, finalizeOrderButton, suggestedItems,
                    new MenuPreviewLayout(menuCategory, menuItem, table));
        }
    }

    private Component createEmptyStateComponent() {
        Span emptyCart = new Span("The cart is empty. Add something to make an order.");
        emptyCart.getStyle().set("font-size", "18px");
        emptyCart.getStyle().set("text-align", "center");

        Image emptyStateImage = new Image("images/cart-is-empty.png", "No data available");
        emptyStateImage.setWidth("100%");

        return new VerticalLayout(emptyStateImage, emptyCart);
    }

    private void configureGrid() {
        // Retrieve the cart items from the session
        cartItems = (List<MenuItemsOrder>) vaadinSession.getAttribute("cartItems");
        grid.removeAllColumns();

        if (cartItems != null && !cartItems.isEmpty())
            grid.setItems(cartItems);

        grid.addClassName("cart-items-grid");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.addColumn(createItemRenderer());
        grid.addColumn(new ComponentRenderer<>(this::quantityField))
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
            new ComponentRenderer<>(Button::new, (button, item) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                        ButtonVariant.LUMO_ERROR,
                        ButtonVariant.LUMO_SMALL);
                button.getElement().setAttribute("aria-label", "Delete");
                button.setIcon(new Icon(VaadinIcon.TRASH));
                button.addClickListener(e -> {
                    assert cartItems != null;
                    cartItems.remove(item);
                    grid.getDataProvider().refreshAll();

                    totalAmountLabel.setText("Total amount to pay: " + getTotalPrice());
                    totalTimeToProcess.setText("Total time to process: " +  getTotalTimeToProcess());
                    header.setText("Cart " + getTotalItemsCount());

                    suggestedItemsData();
                    updateContent();
                });
            })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
    }

    private void createButtonsLayout() {
        finalizeOrderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        if (cartItems == null || cartItems.isEmpty())
            finalizeOrderButton.setEnabled(false);

        finalizeOrderButton.addClickListener(e -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                finalizeOrder();
            }
        });

    }

    private VerticalLayout setMenuLayout () {
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setWidthFull();
        H1 title = new H1("iWaitLess | Cart");
        title.getStyle().set("font-size", "var(--lumo-font-size-xl)")
                .set("margin", "var(--lumo-space-xs) var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs) var(--lumo-space-xs)");
        menuLayout.add(title);
        menuLayout.addClassName("fixed-menu-bar");
        menuLayout.addClassNames(LumoUtility.Background.CONTRAST_5);

        return menuLayout;
    }

    private void finalizeOrder() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Finalize order");
        dialog.add("Are you sure you want to finalize order?");

        Button finalizeButton = new Button("Finalize", (e) -> dialog.close());
        finalizeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);
        finalizeButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(finalizeButton);
        finalizeButton.addClickListener(e -> {
            new CreateOrder(ordersService, orderDetailsService, menuItem,
                    tableEmployeeRelation, orderStatusService, table);

            getUI().ifPresent(ui -> ui.navigate(OrderStatusView.class));
        });

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    private void suggestedItemsData() {
        suggestedItems.removeAll();
        if (cartItems == null || cartItems.isEmpty())
            return;

        List<MenuItems> items = menuItem.findAvailableItems();
        List<MenuItems> commonItems = new ArrayList<>();
        List<Long> currentOrderItemIds = cartItems.stream()
                .map(MenuItemsOrder::getItemId)
                .toList();

        if (items != null && !items.isEmpty()) {
            OrderedList imageContainer = new OrderedList();
            imageContainer.addClassNames(LumoUtility.Gap.SMALL, LumoUtility.Display.GRID,
                    LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);

            for (MenuItems item : items)
                commonItems.addAll(orderDetailsService.findCommonOrderedItems(item));

            if (!commonItems.isEmpty()) {
                Set<MenuItems> uniqueCommonItems = new HashSet<>();
                commonItems.stream()
                        .filter(item -> !currentOrderItemIds.contains(item.getItemId()))
                        .forEach(item -> {
                            if (item.isAvailable() && uniqueCommonItems.stream()
                                    .noneMatch(existingItem -> existingItem.getItemId().equals(item.getItemId()))) {
                                uniqueCommonItems.add(item);
                            }
                        });

                uniqueCommonItems.stream()
                        .limit(10)
                        .forEach(item -> imageContainer.add(new MenuItemViewCard(item, table)));

                if (!uniqueCommonItems.isEmpty())
                    suggestedItems.add(new Span("You may also like:"), imageContainer);
            }
        }
    }

    private Component quantityField(MenuItemsOrder order) {
        IntegerField quantity = new IntegerField();
        quantity.addClassName("quantity-integer-field");
        quantity.setStepButtonsVisible(true);
        quantity.setMin(1);
        quantity.setMax(9);

        quantity.setValue(order.getQuantity());
        quantity.setReadOnly(false);
        quantity.addValueChangeListener(e -> {
            order.setQuantity(e.getValue());
            grid.getDataProvider().refreshAll();

            totalAmountLabel.setText("Total amount to pay: " + getTotalPrice());
            totalTimeToProcess.setText("Total time to process: " +  getTotalTimeToProcess());

            updateContent();
        });
        return quantity;
    }

    private static Renderer<MenuItemsOrder> createItemRenderer() {
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
}
