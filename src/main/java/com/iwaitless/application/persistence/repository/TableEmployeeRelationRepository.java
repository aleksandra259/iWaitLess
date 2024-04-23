package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.TableEmployeeRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableEmployeeRelationRepository extends JpaRepository<TableEmployeeRelation, Long> {

    List<TableEmployeeRelation> findAllByEmployee(Staff employeeId);
    List<TableEmployeeRelation> findAllByTable(RestaurantTable table);
}
