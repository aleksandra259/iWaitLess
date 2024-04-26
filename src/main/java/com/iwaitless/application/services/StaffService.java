package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.Authorities;
import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.UserStaffRelation;
import com.iwaitless.application.persistence.entity.Users;
import com.iwaitless.application.persistence.entity.nomenclatures.StaffRole;
import com.iwaitless.application.persistence.repository.StaffRepository;
import com.iwaitless.application.persistence.repository.UserStaffRelationRepository;
import com.iwaitless.application.persistence.repository.nomenclatures.StaffRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final UserStaffRelationRepository userStaffRepository;

    @Autowired
    private UserService userService;

    public StaffService(StaffRepository staffRepository,
                        StaffRoleRepository staffRoleRepository,
                        UserStaffRelationRepository userStaffRepository) {
        this.staffRepository = staffRepository;
        this.staffRoleRepository = staffRoleRepository;
        this.userStaffRepository = userStaffRepository;
    }

    public List<Staff> findAllEmployees(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return staffRepository.findAll();
        } else {
            return staffRepository.search(stringFilter);
        }
    }

    public Staff findEmployeeByUsername(String username) {
        return staffRepository.findStaff(username);
    }

    public void deleteEmployee(Staff staff) {
        UserStaffRelation userStaffRelation = userStaffRepository
                .findAll()
                .stream()
                .filter(rel -> rel.getEmployee().getEmployeeId().equals(staff.getEmployeeId()))
                .findFirst()
                .orElse(null);
        userStaffRepository.delete(Objects.requireNonNull(userStaffRelation));
        staffRepository.delete(staff);
    }

    public void saveEmployee(Staff contact) {
        if (contact == null) {
            System.err.println("Employee is null. Are you sure you have connected your form to the application?");
            return;
        }
        staffRepository.save(contact);
        saveUserRelation(contact);
    }

    private void saveUserRelation(Staff staff) {
        long userCounter = userStaffRepository
                .findAll()
                .stream()
                .filter(users -> users.getEmployee().getEmployeeId().equals(staff.getEmployeeId())).count();
        if(userCounter == 0) {
            UserStaffRelation userStaffRelation = new UserStaffRelation();
            userStaffRelation.setEmployee(staff);

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            int num0 = Integer.parseInt(formatter.format(staff.getBirthdate()).substring(7));
            if (num0 == 0)
                num0 = 1;
            int num1 = (Integer.parseInt(formatter.format(staff.getBirthdate()).substring(0,2))
                    + Integer.parseInt(formatter.format(staff.getBirthdate()).substring(3,5))
                    / num0);
            //day + month / year in order to give unique digit

            num0 = Integer.parseInt(formatter.format(staff.getBirthdate()).substring(1,2));
            //to ensure username is unique

            String username = staff.getLastName().charAt(0)
                    + staff.getFirstName()
                    + num0 + num1;
            username = username.toLowerCase();
            userStaffRelation.setUsername(username);
            userStaffRepository.save(userStaffRelation);

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String bCryptPassword = "{bcrypt}"+bCryptPasswordEncoder.encode(username);

            Users user = new Users(username, bCryptPassword, true);
            Authorities authority = new Authorities();
            authority.setUsername(user);

            if ("KT".equals(staff.getRole().getId()))
                authority.setAuthority("ROLE_USER_KT");
            else if ("MG".equals(staff.getRole().getId()))
                authority.setAuthority("ROLE_ADMIN");
            else
                authority.setAuthority("ROLE_USER_ST");

            userService.saveUser(user, authority);
        }
    }

    public List<StaffRole> findAllRoles() {
        return staffRoleRepository.findAll();
    }

    public List<UserStaffRelation> finsAllUsers() {
        return userStaffRepository.findAll();
    }

    public UserStaffRelation finsUserByEmployeeId(Long id) {
        return finsAllUsers()
                .stream()
                .filter(user ->
                        user.getEmployee().getEmployeeId().equals(id))
                .findFirst()
                .orElse(null);
    }
}