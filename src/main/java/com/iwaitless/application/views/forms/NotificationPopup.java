package com.iwaitless.application.views.forms;

import com.iwaitless.application.persistence.entity.Notifications;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class NotificationPopup extends FormLayout {

    Dialog dialog = new Dialog();
    Button close = new Button(new Icon(VaadinIcon.CLOSE));
    Button confirm = new Button("Потвърди");


    public NotificationPopup(Notifications notification) {
        getStyle().set("width", "30rem").set("max-width", "100%");
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.START, LumoUtility.BorderRadius.NONE,
                LumoUtility.Margin.NONE);

        dialog.setHeaderTitle("Известие");

        Paragraph description;
        if ("2".equals(notification.getType().getId())) {
            description = new Paragraph("Маса #"
                    + notification.getTable().getTableNo()
                    + " има нужда от съдействие.");
        } else {
            description = new Paragraph("Маса #"
                    + notification.getTable().getTableNo()
                    + " иска сметката.");
        }
        description.addClassNames(LumoUtility.FontSize.LARGE);

        createButtonsLayout();

        HorizontalLayout dialogHeader = new HorizontalLayout(close);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        dialogHeader.setWidthFull();
        dialog.getHeader().add(dialogHeader);
        dialog.setDraggable(true);

        dialog.add(description);
        dialog.getFooter().add(confirm);
        dialog.open();
    }

    private void createButtonsLayout() {
        close.addClickShortcut(Key.ESCAPE);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        close.addClickListener(event -> dialog.close());

        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirm.addClickListener(event -> dialog.close());
    }
}


