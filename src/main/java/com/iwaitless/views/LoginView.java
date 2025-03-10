package com.iwaitless.views;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | iWaitLess")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("login-view");

        LoginI18n i18n = LoginI18n.createDefault();
        Image logo = new Image("images/logo.png", "iWaitLess Logo");
        logo.setWidth("23.9%");

        LoginI18n.Form login = i18n.getForm();
        login.setTitle("Вход в системата");
        login.setUsername("Потребителско име");
        login.setPassword("Парола");
        login.setSubmit("Вход");
        login.setForgotPassword("Забравена парола");
        i18n.setForm(login);


        LoginForm loginForm = new LoginForm();
        loginForm.setI18n(i18n);
        add(logo, loginForm);
        loginForm.setAction("login");

        loginForm.getElement().setAttribute("no-autofocus", "");
    }
}
