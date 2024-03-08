package com.iwaitless.application.views;

import com.iwaitless.application.services.RestaurantTableService;
import com.iwaitless.application.services.StaffService;
import com.iwaitless.application.services.TableEmployeeRelationService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Home Page")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomePageView extends VerticalLayout {

    RestaurantTableService restaurantTable;
    TableEmployeeRelationService tableRelationService;
    StaffService staffService;

    public HomePageView(RestaurantTableService restaurantTable,
                        TableEmployeeRelationService tableRelationService,
                        StaffService staffService) {
        this.restaurantTable = restaurantTable;
        this.tableRelationService = tableRelationService;
        this.staffService = staffService;

    }

}