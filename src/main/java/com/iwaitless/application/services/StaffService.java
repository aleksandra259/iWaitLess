package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.Staff;
import com.iwaitless.application.persistence.entity.UserStaffRelation;
import com.iwaitless.application.persistence.entity.nomenclatures.StaffRole;
import com.iwaitless.application.persistence.repository.StaffRepository;
import com.iwaitless.application.persistence.repository.UserStaffRelationRepository;
import com.iwaitless.application.persistence.repository.nomenclatures.StaffRoleRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final UserStaffRelationRepository userStaffRepository;

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
        saveUserRelation(contact);
    }

    private void saveUserRelation(Staff staff) {
        long userCounter = userStaffRepository
                .findAll()
                .stream()
                .filter(users -> users.getEmployeeId().getEmployeeId().equals(staff.getEmployeeId())).count();
        if(userCounter == 0) {
            UserStaffRelation userStaffRelation = new UserStaffRelation();
            userStaffRelation.setEmployeeId(staff);

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            int num1 = (Integer.parseInt(formatter.format(staff.getBirthdate()).substring(0,2))
                    + Integer.parseInt(formatter.format(staff.getBirthdate()).substring(3,5))
                    / Integer.parseInt(formatter.format(staff.getBirthdate()).substring(7)));
            //day + month / year in order to give unique digit

            int num2 = Integer.parseInt(formatter.format(staff.getBirthdate()).substring(1,2));
            //to ensure username is unique

            String username = staff.getLastName().substring(0,(staff.getLastName().length()-1))
                    + staff.getFirstName().charAt(0) + num1 + num2;
            username = username.toLowerCase();
            userStaffRelation.setUsername(username);
            userStaffRepository.save(userStaffRelation);

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String bCryptPassword = bCryptPasswordEncoder.encode(username);

//            User user = new User(username, "{bcrypt}" + bCryptPassword,
//                                 List.of(new SimpleGrantedAuthority("USER")));
//            userRepository.save(user);
            UserDetails admin = User.builder()
                    .username("admin")
                    .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                    .roles("USER", "ADMIN")
                    .build();
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
                        user.getEmployeeId().getEmployeeId().equals(id))
                .findFirst()
                .orElse(null);
    }

}