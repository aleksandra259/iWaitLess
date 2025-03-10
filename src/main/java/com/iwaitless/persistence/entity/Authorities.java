package com.iwaitless.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "AUTHORITIES")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Authorities {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    private Users username;

    @Column(name = "AUTHORITY")
    private String authority;
}
