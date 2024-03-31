package com.iwaitless.application.views.forms;

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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("iWaitLess|Call Waiter")
@Route("call-waiter-popup")
@AnonymousAllowed
public class CallWaiterPopup extends VerticalLayout {

    Dialog dialog;
    Button close = new Button(new Icon(VaadinIcon.CLOSE));

    Select<String> paymentMethodSelect = new Select<>();
    String tableNo;

    VaadinSession vaadinSession = VaadinSession.getCurrent();

    public CallWaiterPopup() {
        this.tableNo = (String)vaadinSession.getAttribute("tableNo");

        Dialog dialog = createDialog();

        paymentMethodSelect.setItems("Credit Card", "Debit Card", "Cash");
        paymentMethodSelect.setLabel("Payment Method");

        dialog.open();
    }

    private Dialog createDialog() {
        dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.setHeaderTitle("Choose an option:");

        VerticalLayout dialogLayout = createButtonsLayout();
        dialogLayout.setSpacing(false);
        dialog.add(dialogLayout);

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
        callWaitressButton.addClickListener(e -> {
            dialog.close();
            navigateBack();
            Notification.show("Your waitress is notified", 3000,
                            Notification.Position.TOP_STRETCH)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        Button askBillButton = new Button("Ask Bill", VaadinIcon.DOLLAR.create());
        askBillButton.addClickListener(e -> {
            if (paymentMethodSelect.isEmpty()) {
                Notification.show("Please select a payment method", 3000, Notification.Position.TOP_STRETCH)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                dialog.close();
                navigateBack();
                Notification.show("Bill requested with payment method: " + paymentMethodSelect.getValue(),
                                3000, Notification.Position.TOP_STRETCH)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

        HorizontalLayout payWay = new HorizontalLayout(paymentMethodSelect, askBillButton);
        payWay.setAlignItems(Alignment.BASELINE);
        payWay.setSpacing(false);

        return new VerticalLayout(callWaitressButton, payWay);
    }

    private void navigateBack() {
        UI.getCurrent().getPage().executeJs("history.back()");
    }
}

