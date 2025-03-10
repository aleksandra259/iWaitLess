package com.iwaitless.services;

import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.entity.Staff;
import com.iwaitless.persistence.entity.TableEmployeeRelation;
import com.iwaitless.persistence.repository.RestaurantTableRepository;
import com.iwaitless.persistence.repository.TableEmployeeRelationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TableEmployeeRelationService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final TableEmployeeRelationRepository tableEmployeeRelationRepository;


    public TableEmployeeRelationService(RestaurantTableRepository restaurantTableRepository,
                                        TableEmployeeRelationRepository tableEmployeeRelationRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableEmployeeRelationRepository = tableEmployeeRelationRepository;
    }

    public List<RestaurantTable> findAllAssignedTables(Staff employee) {
        List<RestaurantTable> tables = new ArrayList<>();

        tableEmployeeRelationRepository.findAllByEmployee(employee)
                .stream()
                .filter(rel -> "A".equals(rel.getStatus()))
                .forEach(rel -> tables.add(restaurantTableRepository
                        .findById(rel.getTable().getTableId())
                        .orElse(null)));

        return tables;
    }

    public TableEmployeeRelation findTableRelationByTable(RestaurantTable table) {
        return tableEmployeeRelationRepository
                .findAllByTable(table)
                .stream()
                .filter(rel -> "A".equals(rel.getStatus()))
                .findFirst()
                .orElse(null);
    }

    public Staff findAssignedTo (RestaurantTable table) {
        TableEmployeeRelation relation = tableEmployeeRelationRepository
                .findAllByTable(table)
                .stream()
                .filter(rel -> "A".equals(rel.getStatus()))
                .findFirst()
                .orElse(null);

        return relation != null ? relation.getEmployee() : null;
    }

    public void assignTable(TableEmployeeRelation table) {
        if (table == null) {
            throw new IllegalArgumentException("Cannot save a null table relation.");
        }
        tableEmployeeRelationRepository.save(table);
    }

    public void saveAssignTable(TableEmployeeRelation table) {
        tableEmployeeRelationRepository.save(table);
    }
}