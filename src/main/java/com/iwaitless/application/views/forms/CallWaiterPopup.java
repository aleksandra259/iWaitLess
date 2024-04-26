package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.Notifications;
import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationTypes;
import com.iwaitless.application.services.NotificationsService;
import com.iwaitless.application.services.RestaurantTableService;
import com.iwaitless.application.services.TableEmployeeRelationService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.sql.Timestamp;
import java.time.Instant;

@PageTitle("iWaitLess|Call Waiter")
@Route("call-waiter-popup")
@AnonymousAllowed
public class CallWaiterPopup extends VerticalLayout {

    private final NotificationsService notificationsService;
    private final RestaurantTableService tableService;
    private final TableEmployeeRelationService tableEmployeeRelService;
    private final String tableNo;

    Dialog dialog;
    Button close = new Button(new Icon(VaadinIcon.CLOSE));


    public CallWaiterPopup(NotificationsService notificationsService,
                           RestaurantTableService tableService,
                           TableEmployeeRelationService tableEmployeeRelService) {
        this.tableNo = (String)VaadinSession.getCurrent().getAttribute("tableNo");
        this.notificationsService = notificationsService;
        this.tableService = tableService;
        this.tableEmployeeRelService = tableEmployeeRelService;

        Dialog dialog = createDialog();
        dialog.setDraggable(true);
        dialog.open();
    }

    private Dialog createDialog() {
        dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setHeaderTitle("Contact your waiter");
        dialog.add(createButtonsLayout());

        HorizontalLayout dialogHeader = new HorizontalLayout();
        dialogHeader.add(close);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        dialogHeader.setWidthFull();
        dialog.getHeader().add(dialogHeader);

        return dialog;
    }

    private VerticalLayout createButtonsLayout() {
        close.addClickShortcut(Key.ESCAPE);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        close.addClickListener(event -> {
            dialog.close();
            navigateBack();
        });

        Button callWaitressButton = new Button("Call Waitress", VaadinIcon.PHONE.create());
        callWaitressButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        callWaitressButton.addClassName("order-status-link-button");
        callWaitressButton.addClickListener(e -> {
            dialog.close();
            navigateBack();
            saveNotification("2");
            Notification.show("Your waitress is notified", 3000,
                            Notification.Position.TOP_STRETCH)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        Button askBillButton = new Button("Ask Bill", VaadinIcon.DOLLAR.create());
        askBillButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        askBillButton.addClassName("order-status-link-button");
        askBillButton.addClickListener(e -> {
            dialog.close();
            navigateBack();
            saveNotification("3");
            Notification.show("Your waitress is notified", 3000,
                            Notification.Position.TOP_STRETCH)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        return new VerticalLayout(callWaitressButton, askBillButton);
    }

    private void saveNotification (String type) {
        RestaurantTable table = tableService.findTableByTableNo(tableNo);

        NotificationTypes notificationType = new NotificationTypes();
        notificationType.setId(type);
        NotificationStatus notificationStatus = new NotificationStatus();
        notificationStatus.setId("U");

        Notifications notification = new Notifications();
        notification.setEmployee(tableEmployeeRelService.findTableRelationByTable(table).getEmployee());
        notification.setType(notificationType);
        notification.setStatus(notificationStatus);
        notification.setTable(table);
        notification.setRegistrationDate(Timestamp.from(Instant.now()));

        notificationsService.saveNotification(notification);
    }

    private void navigateBack() {
        UI.getCurrent().getPage().executeJs("history.back()");
    }
}

