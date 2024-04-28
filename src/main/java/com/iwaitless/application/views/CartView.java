package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItems;
import com.iwaitless.application.persistence.entity.MenuItemsOrder;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.*;
import com.iwaitless.application.views.utility.CreateOrder;
import com.iwaitless.application.views.utility.Renderers;
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

@PageTitle("iWaitLess | Количка")
@Route(value = "cart")
@AnonymousAllowed
public class CartView extends VerticalLayout {

    private final MenuCategoryService menuCategory;
    private final MenuItemService menuItem;
    private final OrdersService ordersService;
    private final OrderDetailsService orderDetailsService;
    private final NotificationsService notificationsService;
    private final TableEmployeeRelationService tableEmployeeRelation;


    Span totalAmountLabel = new Span();
    Span totalTimeToProcess = new Span();
    H3 header = new H3();
    List<MenuItemsOrder> cartItems;
    RestaurantTable table = new RestaurantTable();
    Button finalizeOrderButton = new Button("Завърши поръчката");

    Grid<MenuItemsOrder> grid = new Grid<>();
    VerticalLayout suggestedItems = new VerticalLayout();

    VaadinSession vaadinSession = VaadinSession.getCurrent();

    public CartView(MenuCategoryService menuCategory,
                    MenuItemService menuItem,
                    RestaurantTableService restaurantTable,
                    OrdersService ordersService,
                    OrderDetailsService orderDetailsService,
                    NotificationsService notificationsService,
                    TableEmployeeRelationService tableEmployeeRelation) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.ordersService = ordersService;
        this.orderDetailsService = orderDetailsService;
        this.notificationsService = notificationsService;
        this.tableEmployeeRelation = tableEmployeeRelation;

        String tableNo = (String)vaadinSession.getAttribute("tableNo");
        if (tableNo != null)
            table = restaurantTable.findTableByTableNo(tableNo);

        configureGrid();
        createButtonsLayout();
        suggestedItemsData();

        header.setText("Количка " + getTotalItemsCount());
        totalAmountLabel.setText("Обща сума за плащане: " + getTotalPrice());
        totalTimeToProcess.setText("Общо време за обработка: " +  getTotalTimeToProcess());

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

        return String.format("%.2f", sum) + " " + currency;
    }

    private String getTotalTimeToProcess() {
        if (cartItems != null && !cartItems.isEmpty()) {
            double maxTimeToProcess = cartItems.get(0).getTimeToProcess() == null
                    ? 0.0d : cartItems.get(0).getTimeToProcess();

            for (MenuItemsOrder item : cartItems)
                if (item.getTimeToProcess() != null && item.getTimeToProcess() > maxTimeToProcess)
                    maxTimeToProcess = item.getTimeToProcess();

            return maxTimeToProcess == 0.0d ?
                    "Неопределен" : String.format("%.0f", maxTimeToProcess) + " мин.";
        }

        return "Неопределен";
    }

    private String getTotalItemsCount() {
        int itemsCount = 0;
        if (cartItems != null && !cartItems.isEmpty())
            for (MenuItemsOrder ignored : cartItems)
                itemsCount += 1;

        return "(" + itemsCount + " елемента)";
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
        Span emptyCart = new Span("Количката е празна. Добавете нещо, за да направите поръчка.");
        emptyCart.getStyle().set("font-size", "18px");
        emptyCart.getStyle().set("text-align", "center");

        Image emptyStateImage = new Image("images/cart-is-empty.png", "Няма налични данни");
        emptyStateImage.setWidth("100%");

        return new VerticalLayout(emptyStateImage, emptyCart);
    }

    private void configureGrid() {
        // Retrieve the cart items from the session
        cartItems = (List<MenuItemsOrder>) vaadinSession.getAttribute("cartItems");
        grid.removeAllColumns();

        if (cartItems != null && !cartItems.isEmpty())
            grid.setItems(cartItems);

        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_NO_BORDER);

        grid.addColumn(Renderers.createItemOrderRenderer());
        grid.addColumn(new ComponentRenderer<>(this::quantityField))
                .setTextAlign(ColumnTextAlign.END);
        grid.addColumn(
            new ComponentRenderer<>(Button::new, (button, item) -> {
                button.getElement().setAttribute("aria-label", "Delete");
                button.addClassName("delete-button");
                button.setIcon(new Icon(VaadinIcon.TRASH));
                button.addClickListener(e -> {
                    assert cartItems != null;
                    cartItems.remove(item);
                    grid.getDataProvider().refreshAll();

                    totalAmountLabel.setText("Обща сума за плащане: " + getTotalPrice());
                    totalTimeToProcess.setText("Общо време за обработка: " +  getTotalTimeToProcess());
                    header.setText("Количка " + getTotalItemsCount());

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
        Image logo = new Image("images/logo.png", "iWaitLess Logo");
        logo.setWidth("50%");

        VerticalLayout menuLayout = new VerticalLayout(logo);
        menuLayout.setWidthFull();
        menuLayout.addClassName("fixed-menu-bar");

        return menuLayout;
    }

    private void finalizeOrder() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Завърши поръчка");
        dialog.add("Сигурни ли сте, че искате да завършите поръчката?");

        Button finalizeButton = new Button("Завърши", (e) -> dialog.close());
        finalizeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);
        finalizeButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(finalizeButton);
        finalizeButton.addClickListener(e -> {
            new CreateOrder(ordersService, orderDetailsService, notificationsService,
                    menuItem, tableEmployeeRelation, table);

            getUI().ifPresent(ui -> ui.navigate(OrderStatusView.class));
        });

        Button cancelButton = new Button("Отказ", (e) -> dialog.close());
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
            imageContainer.addClassNames("image-gallery-view");
            imageContainer.setWidthFull();
            imageContainer.addClassNames(LumoUtility.Gap.SMALL, LumoUtility.Display.GRID,
                    LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Bottom.XLARGE,
                    LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);

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
                    suggestedItems.add(new Span("Може да харесате и:"), imageContainer);
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

            totalAmountLabel.setText("Обща сума за плащане: " + getTotalPrice());
            totalTimeToProcess.setText("Общо време за обработка: " +  getTotalTimeToProcess());

            updateContent();
        });
        return quantity;
    }
}
