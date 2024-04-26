package com.iwaitless.application.views;

import com.iwaitless.application.authentication.SecurityService;
import com.iwaitless.application.services.*;
import com.iwaitless.application.views.list.NotificationsView;
import com.iwaitless.application.views.list.ListStaffView;
import com.iwaitless.application.views.list.OrdersView;
import com.iwaitless.application.views.list.RestaurantTablesAssignView;
import com.iwaitless.application.views.list.RestaurantTablesConfigurationView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@PermitAll
public class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private final NotificationsService notificationService;
    private final StaffService staffService;
    private final OrdersService orderService;
    private final OrderDetailsService orderDetailService;
    private final OrderStatusService statusService;
    private final String authorities;


    public MainLayout(SecurityService securityService,
                      NotificationsService notificationService,
                      StaffService staffService,
                      OrdersService orderService,
                      OrderDetailsService orderDetailService,
                      OrderStatusService statusService) {
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.staffService = staffService;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.statusService = statusService;
        authorities = securityService.getAuthenticatedUser().getAuthorities().toString();

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("iWaitLess");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        Button logout = new Button("Log out", e -> securityService.logout());

        var header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        if (authorities.contains("ROLE_ADMIN")) {
            RouterLink menuPreview = new RouterLink("Menu Preview", MenuPreviewView.class);
            getUI().ifPresent(ui -> ui.getPage().open(menuPreview.getHref()));
            header.add(menuPreview);
        }
        if (authorities.contains("ROLE_USER_ST")) {
            NotificationsView notificationsPopup =
                    new NotificationsView(notificationService,
                            staffService,
                            orderService,
                            orderDetailService,
                            statusService);
            notificationsPopup.addClassName("notifications-popup");

            Button notificationButton = new Button(VaadinIcon.BELL.create());
            notificationButton.addClickListener(e -> notificationsPopup.open());
            header.add(notificationButton);
        }

        header.add(logout);
        addToNavbar(header);
    }

    private void createDrawer() {
        if (authorities.contains("ROLE_ADMIN")) {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Home Page", HomePageView.class),
                    new RouterLink("Staff List", ListStaffView.class),
                    new RouterLink("Menu Configuration", MenuConfigurationView.class),
                    new RouterLink("Tables Configuration", RestaurantTablesConfigurationView.class),
                    new RouterLink("Orders", OrdersView.class)
            ));
        } else if (authorities.contains("ROLE_USER_ST")) {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Home Page", HomePageView.class),
                    new RouterLink("Tables Assignment", RestaurantTablesAssignView.class),
                    new RouterLink("Orders", OrdersView.class)
            ));
        } else if (authorities.contains("ROLE_USER_KT")) {
            addToDrawer(new VerticalLayout(
                    new RouterLink("Home Page", HomePageView.class),
                    new RouterLink("Orders", OrdersView.class)
            ));
        }
    }
}