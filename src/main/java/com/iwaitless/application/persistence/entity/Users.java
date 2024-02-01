package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Table(name = "USERS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "MENU_ITEM_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "ID")
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @OneToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Staff employeeId;
}
