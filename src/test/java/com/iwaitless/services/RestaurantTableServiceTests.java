package com.iwaitless.services;

import com.iwaitless.domain.RestaurantTableMockData;
import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.repository.RestaurantTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantTableServiceTests {
    @Mock
    private RestaurantTableRepository restaurantTableRepository;
    @InjectMocks
    private RestaurantTableService restaurantTableService;

    private final RestaurantTable table = RestaurantTableMockData.initTable();
    private final List<RestaurantTable> tableList = List.of(table, table);


    @Test
    void testFindAllTables() {
        when(restaurantTableRepository.search("tbl")).thenReturn(tableList);

        List<RestaurantTable> tables = restaurantTableService.findAllTables("tbl");
        assertNotNull(tables);
        assertEquals(2, tables.size());
        assertEquals("tbl_1", tables.get(0).getTableNo());
    }

    @Test
    void testFindTableByTableNo() {
        when(restaurantTableRepository.findByTableNo("tbl_1")).thenReturn(table);

        RestaurantTable result = restaurantTableService.findTableByTableNo("tbl_1");
        assertNotNull(result);
        assertEquals("tbl_1", result.getTableNo());
    }

    @Test
    void testFindTableByTableNo_TableNoNull() {
        assertThrows(IllegalArgumentException.class, () -> restaurantTableService.findTableByTableNo(null));
    }

    @Test
    void testFindTableByTableNo_TableNoEmpty() {
        assertThrows(IllegalArgumentException.class, () -> restaurantTableService.findTableByTableNo(""));
    }

    @Test
    void testDeleteTable() {
        restaurantTableService.deleteTable(table);
        verify(restaurantTableRepository, times(1)).delete(table);
    }

    @Test
    void testDeleteTable_NullTable() {
        assertThrows(IllegalArgumentException.class, () -> restaurantTableService.deleteTable(null));
    }

    @Test
    void testSaveTable() {
        restaurantTableService.saveTable(table);
        verify(restaurantTableRepository, times(1)).save(table);
    }

    @Test
    void testSaveTable_NullTable() {
        assertThrows(IllegalArgumentException.class, () -> restaurantTableService.saveTable(null));
    }
}
