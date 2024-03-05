package com.iwaitless.application.persistence.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "TABLE_EMPLOYEE_RELATION")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TableEmployeeRelation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, insertable = false, unique = true)
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
