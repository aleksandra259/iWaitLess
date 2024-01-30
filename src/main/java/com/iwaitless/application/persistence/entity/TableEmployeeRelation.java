package com.iwaitless.application.persistence.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Table(name = "TABLE_EMPLOYEE_RELATION")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TableEmployeeRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "TABLE_EMPLOYEE_REL_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "TABLE_RELATION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TABLE_ID")
    @NotNull
    private RestaurantTable tableId;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    @NotNull
    private Staff employeeId;
}
