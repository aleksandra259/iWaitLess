package com.iwaitless.application.views.utility;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;

import java.util.Arrays;

public class NotificationButton extends Button {

    private final Element numberOfNotifications;

    public NotificationButton() {
        super(VaadinIcon.BELL_O.create());
        numberOfNotifications = new Element("span");
        numberOfNotifications.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setTransform("translate(-40%, -85%)");
        numberOfNotifications.getThemeList().addAll(
                Arrays.asList("badge", "error", "primary", "small", "pill"));
    }

    public void setUnreadMessages(int unread) {
        numberOfNotifications.setText(unread + "");
        if(unread > 0 && numberOfNotifications.getParent() == null) {
            getElement().appendChild(numberOfNotifications);
        } else if(numberOfNotifications.getNode().isAttached()) {
            numberOfNotifications.removeFromParent();
        }
    }

}
