package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.StaffRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Table(name = "STAFF")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "EMPLOYEE_ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long employeeId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "BIRTHDATE")
    private Date birthdate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")
    private StaffRole role;

    @Column(name = "USERNAME")
    private String username;
}
