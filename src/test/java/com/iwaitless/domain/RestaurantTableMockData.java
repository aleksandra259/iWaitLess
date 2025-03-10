package com.iwaitless.domain;

import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.entity.TableEmployeeRelation;

public class RestaurantTableMockData {
    private RestaurantTableMockData() {}

    public static RestaurantTable initTable() {
        return new RestaurantTable(1L, "tbl_1", "test", null);
    }
    public static TableEmployeeRelation initTableRelation() {
        TableEmployeeRelation relation = new TableEmployeeRelation();
        relation.setEmployee(StaffMockData.initStaff());
        relation.setTable(initTable());
        relation.setStatus("A");

        return relation;
    }



}
