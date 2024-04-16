package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.TableEmployeeRelation;
import com.iwaitless.application.persistence.repository.RestaurantTableRepository;
import com.iwaitless.application.persistence.repository.TableEmployeeRelationRepository;
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

        tableEmployeeRelationRepository.findAllByEmployeeId(employee)
                .stream()
                .filter(rel -> "A".equals(rel.getStatus()))
                .forEach(rel -> {
                    tables.add(restaurantTableRepository
                            .findById(rel.getTableId().getTableId())
                            .orElse(null));
                });

        return tables;
    }

    public TableEmployeeRelation findTableRelationByTable(RestaurantTable table) {
        return tableEmployeeRelationRepository
                .findAllByTableId(table)
                .stream()
                .filter(rel -> "A".equals(rel.getStatus()))
                .findFirst()
                .orElse(null);
    }

    public Staff findAssignedTo (RestaurantTable table) {
        TableEmployeeRelation relation = tableEmployeeRelationRepository
                .findAllByTableId(table)
                .stream()
                .filter(rel -> "A".equals(rel.getStatus()))
                .findFirst()
                .orElse(null);

        return relation != null ? relation.getEmployeeId() : null;
    }

    public void assignTable(TableEmployeeRelation table) {
        if (table == null) {
            System.err.println("Table save failed");
            return;
        }
        tableEmployeeRelationRepository.save(table);
    }

    public void saveAssignTable(TableEmployeeRelation table) {
        tableEmployeeRelationRepository.save(table);
    }



}