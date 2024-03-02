package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;


    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public List<RestaurantTable> findAllTables(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return restaurantTableRepository.findAll();
        } else {
            return restaurantTableRepository.search(stringFilter);
        }
    }

    public RestaurantTable findTableByDesc(String description) {
        return restaurantTableRepository
                .findAll()
                .stream()
                .filter(table -> table.getDescription().contains(description))
                .findFirst()
                .orElse(null);
    }
    public RestaurantTable findTableById(Long id) {
        return restaurantTableRepository
                .findById(id)
                .orElse(null);
    }

    public void deleteTable(RestaurantTable table) {
        restaurantTableRepository.delete(table);
    }

    public void saveTable(RestaurantTable table) {
        if (table == null) {
            System.err.println("Table save failed");
            return;
        }
        restaurantTableRepository.save(table);
    }



}