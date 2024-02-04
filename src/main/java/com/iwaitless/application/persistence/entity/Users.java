package com.iwaitless.application.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "USERS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Users {

    @Id
    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ENABLED")
    private boolean enabled;
}
