package com.iwaitless.application.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table(name = "USER")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Users {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @OneToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Staff employeeId;
}
