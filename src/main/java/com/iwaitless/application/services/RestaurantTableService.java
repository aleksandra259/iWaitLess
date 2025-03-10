package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.RestaurantTable;
import com.iwaitless.application.persistence.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantTableService {

    @Value("${iwaitless.constant.qr.url}")
    public String qrUrl;
    @Value("${iwaitless.constant.qr.directory}")
    public String qrDirectory;

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

    public RestaurantTable findTableByTableNo(String tableNo) {
        if (tableNo == null || tableNo.isEmpty()) {
            throw new IllegalArgumentException("Table number cannot be null or empty.");
        }
        return restaurantTableRepository.findByTableNo(tableNo);
    }

    public void deleteTable(RestaurantTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Cannot delete a null table.");
        }
        restaurantTableRepository.delete(table);
    }

    public void saveTable(RestaurantTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Cannot save a null table.");
        }
        restaurantTableRepository.save(table);
    }
}