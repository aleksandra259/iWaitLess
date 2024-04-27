package com.iwaitless.application.views;

import com.iwaitless.application.authentication.SecurityService;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.services.*;
import com.iwaitless.application.views.list.*;
import com.iwaitless.application.views.utility.NotificationButton;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@PermitAll
public class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private final NotificationsService notificationService;
    private final OrdersService orderService;
    private final OrderDetailsService orderDetailService;
    private final OrderStatusService statusService;
    private final String authorities;
    private final Staff staff;


    public MainLayout(SecurityService securityService,
                      NotificationsService notificationService,
                      StaffService staffService,
                      OrdersService orderService,
                      OrderDetailsService orderDetailService,
                      OrderStatusService statusService) {
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.statusService = statusService;
        this.staff = staffService.findEmployeeByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        authorities = securityService.getAuthenticatedUser().getAuthorities().toString();

        createHeader();

        SideNav nav = createSideNav();
        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
        setPrimarySection(Section.DRAWER);
    }

    private void createHeader() {
        Image logo = new Image("images/logo.png", "iWaitLess Logo");
        logo.addClassNames("logo");

        Button logout = new Button("Log out", e -> securityService.logout());

        var header = new HorizontalLayout(new DrawerToggle(), logo);
        header.addClassName("main-layout");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();

        if (authorities.contains("ROLE_ADMIN")) {
            RouterLink menuPreview = new RouterLink("Menu Preview", MenuPreviewView.class);
            getUI().ifPresent(ui -> ui.getPage().open(menuPreview.getHref()));
            header.add(menuPreview);
        }
        if (authorities.contains("ROLE_USER_ST")) {
            NotificationsView notificationsPopup =
                    new NotificationsView(notificationService,
                            orderService,
                            orderDetailService,
                            statusService,
                            staff);
            notificationsPopup.addClassName("notifications-popup");

            var bellBtn = new NotificationButton();
            bellBtn.setUnreadMessages(notificationService.countUnreadNotificationsByEmployee(staff));
            bellBtn.addClickListener(e -> {
                bellBtn.setUnreadMessages(notificationService.countUnreadNotificationsByEmployee(staff));
                notificationsPopup.open();
            });
            header.add(bellBtn);
        }

        header.add(logout);
        addToNavbar(header);
    }

    private SideNav createSideNav() {
        SideNav nav = new SideNav();
        nav.addClassName("main-drawer");

        if (authorities.contains("ROLE_ADMIN")) {
            nav.addItem(
                    new SideNavItem("Home Page", HomePageView.class, VaadinIcon.HOME.create()),
                    new SideNavItem("Staff List", ListStaffView.class, VaadinIcon.GROUP.create()),
                    new SideNavItem("Menu Configuration", MenuConfigurationView.class, VaadinIcon.CUTLERY.create()),
                    new SideNavItem("Tables Configuration", RestaurantTablesConfigurationView.class, VaadinIcon.LIST.create()),
                    new SideNavItem("Orders", OrdersView.class, VaadinIcon.CART.create())
            );
        } else if (authorities.contains("ROLE_USER_ST")) {
            nav.addItem(
                    new SideNavItem("Home Page", HomePageView.class, VaadinIcon.HOME.create()),
                    new SideNavItem("Tables Assignment", RestaurantTablesAssignView.class, VaadinIcon.LIST.create()),
                    new SideNavItem("Orders", OrdersView.class, VaadinIcon.CART.create())
            );
        } else if (authorities.contains("ROLE_USER_KT")) {
            nav.addItem(
                    new SideNavItem("Home Page", HomePageView.class, VaadinIcon.HOME.create()),
                    new SideNavItem("Orders", OrdersView.class, VaadinIcon.CART.create())
            );
        }
        nav.setWidth("100%");
        nav.setHeight("100%");

        return nav;
    }
}