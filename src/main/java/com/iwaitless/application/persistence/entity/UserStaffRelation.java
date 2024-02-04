package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Table(name = "USER_STAFF_RELATION")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserStaffRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "USER_STAFF_RELATION_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "USERNAME")
    private String username;

    @OneToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Staff employeeId;
}
