package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.nomenclatures.StaffRole;
import com.iwaitless.application.persistence.repository.StaffRepository;
import com.iwaitless.application.persistence.repository.StaffRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;

    public StaffService(StaffRepository staffRepository,
                        StaffRoleRepository staffRoleRepository) {
        this.staffRepository = staffRepository;
        this.staffRoleRepository = staffRoleRepository;
    }

    public List<Staff> findAllEmployees(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return staffRepository.findAll();
        } else {
            return staffRepository.search(stringFilter);
        }
    }

    public long countEmployees() {
        return staffRepository.count();
    }

    public void deleteEmployee(Staff contact) {
        staffRepository.delete(contact);
    }

    public void saveEmployee(Staff contact) {
        if (contact == null) {
            System.err.println("Employee is null. Are you sure you have connected your form to the application?");
            return;
        }
        staffRepository.save(contact);
    }

    public List<StaffRole> findAllRoles() {
        return staffRoleRepository.findAll();
    }

}