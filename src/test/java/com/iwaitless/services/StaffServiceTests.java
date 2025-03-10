package com.iwaitless.services;

import com.iwaitless.domain.StaffMockData;
import com.iwaitless.persistence.entity.Staff;
import com.iwaitless.persistence.entity.UserStaffRelation;
import com.iwaitless.persistence.entity.nomenclatures.StaffRole;
import com.iwaitless.persistence.repository.StaffRepository;
import com.iwaitless.persistence.repository.UserStaffRelationRepository;
import com.iwaitless.persistence.repository.nomenclatures.StaffRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceTests {
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private StaffRoleRepository staffRoleRepository;
    @Mock
    private UserStaffRelationRepository userStaffRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private StaffService staffService;

    private final Staff staff = StaffMockData.initStaff();
    private final StaffRole staffRole = StaffMockData.initStaffRole();
    private final UserStaffRelation userStaffRelation = StaffMockData.initUserStaffRelation();
    private final List<Staff> staffList = List.of(staff);


    @Test
    void testFindAllEmployees_WithFilter() {
        when(staffRepository.search("john")).thenReturn(staffList);

        List<Staff> result = staffService.findAllEmployees("john");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(staff.getEmployeeId(), result.get(0).getEmployeeId());
    }

    @Test
    void testFindAllEmployees_NoFilter() {
        when(staffRepository.findAll()).thenReturn(staffList);

        List<Staff> result = staffService.findAllEmployees("");
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindEmployeeByUsername() {
        when(staffRepository.findStaff("john.doe")).thenReturn(staff);

        Staff result = staffService.findEmployeeByUsername("john.doe");
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testDeleteEmployee() {
        when(userStaffRepository.findAll()).thenReturn(List.of(userStaffRelation));

        staffService.deleteEmployee(staff);
        verify(userStaffRepository, times(1)).delete(userStaffRelation);
        verify(staffRepository, times(1)).delete(staff);
    }

    @Test
    void testDeleteEmployee_UserRelationNotFound() {
        when(userStaffRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> staffService.deleteEmployee(staff));
    }

    @Test
    void testSaveEmployee() {
        when(userStaffRepository.findAll()).thenReturn(List.of());
        when(staffRepository.save(any(Staff.class))).thenReturn(staff);

        staffService.saveEmployee(staff);
        verify(staffRepository, times(2)).save(staff);
        verify(userStaffRepository, times(1)).save(any(UserStaffRelation.class));
        verify(userService, times(1)).saveUser(any(), any());
    }

    @Test
    void testSaveEmployee_NullEmployee() {
        assertThrows(IllegalArgumentException.class, () -> staffService.saveEmployee(null));
    }

    @Test
    void testFindAllRoles() {
        when(staffRoleRepository.findAll()).thenReturn(List.of(staffRole));

        List<StaffRole> roles = staffService.findAllRoles();
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("Kitchen Staff", roles.get(0).getName());
    }

    @Test
    void testFindAllUsers() {
        when(userStaffRepository.findAll()).thenReturn(List.of(userStaffRelation));

        List<UserStaffRelation> users = staffService.finsAllUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("john.doe", users.get(0).getUsername());
    }

    @Test
    void testFindUserByEmployeeId() {
        when(userStaffRepository.findAll()).thenReturn(List.of(userStaffRelation));

        UserStaffRelation result = staffService.finsUserByEmployeeId(1L);
        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
    }

    @Test
    void testFindUserByEmployeeId_NotFound() {
        when(userStaffRepository.findAll()).thenReturn(List.of());

        UserStaffRelation result = staffService.finsUserByEmployeeId(1L);
        assertNull(result);
    }
}

