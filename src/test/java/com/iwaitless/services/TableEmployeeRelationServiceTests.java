package com.iwaitless.services;

import com.iwaitless.domain.RestaurantTableMockData;
import com.iwaitless.domain.StaffMockData;
import com.iwaitless.persistence.entity.RestaurantTable;
import com.iwaitless.persistence.entity.Staff;
import com.iwaitless.persistence.entity.TableEmployeeRelation;
import com.iwaitless.persistence.repository.RestaurantTableRepository;
import com.iwaitless.persistence.repository.TableEmployeeRelationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableEmployeeRelationServiceTests {
    @Mock
    private RestaurantTableRepository restaurantTableRepository;
    @Mock
    private TableEmployeeRelationRepository tableEmployeeRelationRepository;
    @InjectMocks
    private TableEmployeeRelationService tableEmployeeRelationService;

    private final Staff staff = StaffMockData.initStaff();
    private final RestaurantTable table = RestaurantTableMockData.initTable();
    private final TableEmployeeRelation relation = RestaurantTableMockData.initTableRelation();
    private final List<TableEmployeeRelation> relationList = List.of(relation);


    @Test
    void testFindAllAssignedTables() {
        when(tableEmployeeRelationRepository.findAllByEmployee(staff)).thenReturn(relationList);
        when(restaurantTableRepository.findById(anyLong())).thenReturn(Optional.of(table));

        List<RestaurantTable> assignedTables = tableEmployeeRelationService.findAllAssignedTables(staff);
        assertNotNull(assignedTables);
        assertEquals(1, assignedTables.size());
        assertEquals("tbl_1", assignedTables.get(0).getTableNo());
    }

    @Test
    void testFindAllAssignedTables_NoAssignedTables() {
        when(tableEmployeeRelationRepository.findAllByEmployee(staff)).thenReturn(List.of());

        List<RestaurantTable> assignedTables = tableEmployeeRelationService.findAllAssignedTables(staff);
        assertNotNull(assignedTables);
        assertTrue(assignedTables.isEmpty());
    }

    @Test
    void testFindTableRelationByTable() {
        when(tableEmployeeRelationRepository.findAllByTable(table)).thenReturn(relationList);

        TableEmployeeRelation result = tableEmployeeRelationService.findTableRelationByTable(table);
        assertNotNull(result);
        assertEquals("A", result.getStatus());
        assertEquals(staff.getEmployeeId(), result.getEmployee().getEmployeeId());
    }

    @Test
    void testFindTableRelationByTable_NoRelation() {
        when(tableEmployeeRelationRepository.findAllByTable(table)).thenReturn(List.of());

        TableEmployeeRelation result = tableEmployeeRelationService.findTableRelationByTable(table);
        assertNull(result);
    }

    @Test
    void testFindAssignedTo() {
        when(tableEmployeeRelationRepository.findAllByTable(table)).thenReturn(relationList);

        Staff assignedStaff = tableEmployeeRelationService.findAssignedTo(table);
        assertNotNull(assignedStaff);
        assertEquals(1L, assignedStaff.getEmployeeId());
    }

    @Test
    void testFindAssignedTo_NoRelation() {
        when(tableEmployeeRelationRepository.findAllByTable(table)).thenReturn(List.of());

        Staff assignedStaff = tableEmployeeRelationService.findAssignedTo(table);
        assertNull(assignedStaff);
    }

    @Test
    void testAssignTable() {
        tableEmployeeRelationService.assignTable(relation);
        verify(tableEmployeeRelationRepository, times(1)).save(relation);
    }

    @Test
    void testAssignTable_NullTable() {
        assertThrows(IllegalArgumentException.class, () -> tableEmployeeRelationService.assignTable(null));
    }

    @Test
    void testSaveAssignTable() {
        tableEmployeeRelationService.saveAssignTable(relation);
        verify(tableEmployeeRelationRepository, times(1)).save(relation);
    }
}

