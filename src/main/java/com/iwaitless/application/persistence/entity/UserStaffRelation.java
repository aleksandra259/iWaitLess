package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "USER_STAFF_RELATION")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserStaffRelation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long id;

    @NotNull
    @Column(name = "USERNAME")
    private String username;

    @OneToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Staff employee;
}
