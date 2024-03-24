package com.iwaitless.application.views;

import com.iwaitless.application.persistence.entity.MenuItemsOrder;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.services.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@PageTitle("iWaitLess | Cart")
@Route(value = "cart")
@AnonymousAllowed
public class CartView extends VerticalLayout  implements HasUrlParameter<String> {

    MenuCategoryService menuCategory;
    MenuItemService menuItem;
    RestaurantTableService restaurantTable;
    OrdersService ordersService;
    OrderDetailsService orderDetailsService;

    Div content = new Div();
    H3 totalAmountLabel = new H3();
    H2 header = new H2();
    List<MenuItemsOrder> cartItems;
    RestaurantTable table;
    String tableNo;

    Grid<MenuItemsOrder> grid = new Grid<>();

    public CartView(MenuCategoryService menuCategory,
                    MenuItemService menuItem,
                    RestaurantTableService restaurantTable,
                    OrdersService ordersService,
                    OrderDetailsService orderDetailsService) {
        this.menuCategory = menuCategory;
        this.menuItem = menuItem;
        this.restaurantTable = restaurantTable;
        this.ordersService = ordersService;
        this.orderDetailsService = orderDetailsService;

        configureGrid();

        Button finalizeOrderButton = new Button("Finalize Order");

        header.setText("Cart " + getTotalItemsCount());
        totalAmountLabel.setText("Total amount to pay: " + getTotalPrice());

        H3 youMayAlsoLikeLabel = new H3("You may also like:");

        add(header, grid, finalizeOrderButton, totalAmountLabel, youMayAlsoLikeLabel, content);
    }

    private String getTotalPrice() {
        double sum = .0d;
        String currency = "EUR";
        if (cartItems != null)
            for (MenuItemsOrder item : cartItems) {
                sum += item.getQuantity() * item.getPrice();
                currency = item.getCurrency().getCurrencyCode();
            }

        return sum + " " + currency;
    }
    private String getTotalItemsCount() {
        int itemsCount = 0;
        if (cartItems != null)
            for (MenuItemsOrder ignored : cartItems)
                itemsCount += 1;

        return "(" + itemsCount + " elements)";
    }

    private void configureGrid() {
        // Retrieve the cart items from the session
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        cartItems = (List<MenuItemsOrder>) vaadinSession.getAttribute("cartItems");

        if (cartItems != null && !cartItems.isEmpty())
            grid.setItems(cartItems);

        grid.addClassName("cart-items-grid");
        grid.setWidthFull();
        grid.setAllRowsVisible(true);
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
                    header.setText("Cart " + getTotalItemsCount());
                });
            })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
    }

    private Component quantityField(MenuItemsOrder order) {
        IntegerField quantity = new IntegerField();
        quantity.addClassName("quantity-integer-field");
        quantity.setStepButtonsVisible(true);
        quantity.setMin(0);
        quantity.setMax(9);

        quantity.setValue(order.getQuantity());
        quantity.setReadOnly(false);
        quantity.addValueChangeListener(e -> {
            order.setQuantity(e.getValue());
            grid.getDataProvider().refreshItem(order);
            totalAmountLabel.setText("Total amount to pay: " + getTotalPrice());
            header.setText("Cart " + getTotalItemsCount());
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
                .withProperty("price", MenuItemsOrder::getPrice)
                .withProperty("currency", e -> e.getCurrency().getCurrencyCode());
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        tableNo = MenuPreviewLayout.getTableNo(parameter);

        if (!tableNo.isEmpty()) {
            table = restaurantTable.findTableByTableNo(tableNo);

            content.add(new MenuPreviewLayout(menuCategory,
                    menuItem,
                    table));
        }
    }
}
