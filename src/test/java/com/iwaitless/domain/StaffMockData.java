package com.iwaitless.domain;

import com.iwaitless.persistence.entity.Staff;
import com.iwaitless.persistence.entity.UserStaffRelation;
import com.iwaitless.persistence.entity.nomenclatures.StaffRole;

import java.time.Instant;
import java.util.Date;

public class StaffMockData {
    private StaffMockData() {}

    public static Staff initStaff() {
        Staff employee = new Staff(1L, "John", "Doe", "john.doe@example.com",
                null, null, Date.from(Instant.parse("1980-04-09T10:15:30.00Z")), null, "john.doe");
        employee.setRole(initStaffRole());

        return employee;
    }

    public static StaffRole initStaffRole() {
        StaffRole staffRole = new StaffRole();
        staffRole.setId("KT");
        staffRole.setName("Kitchen Staff");

        return staffRole;
    }

    public static UserStaffRelation initUserStaffRelation() {
        UserStaffRelation userStaffRelation = new UserStaffRelation();
        userStaffRelation.setEmployee(initStaff());
        userStaffRelation.setUsername("john.doe");

        return userStaffRelation;
    }

}
