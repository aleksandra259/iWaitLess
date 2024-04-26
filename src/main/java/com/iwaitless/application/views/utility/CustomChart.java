package com.iwaitless.application.views.utility;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class CustomChart extends HorizontalLayout {

    public CustomChart() {
        Image foodChart = new Image("images/ordered_food_chart.png", "");
        Image revenueChart = new Image("images/revenue_chart.png", "");

        foodChart.setWidth("43%");
        foodChart.setHeight("70%");
        revenueChart.setWidth("55%");
        revenueChart.setHeight("65%");

        add(revenueChart, foodChart);
    }

}