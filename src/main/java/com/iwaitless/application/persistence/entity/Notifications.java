package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationTypes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "NOTIFICATIONS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Notifications {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Staff employeeId;

    @ManyToOne
    @JoinColumn(name = "TYPE", referencedColumnName = "ID")
    private NotificationTypes type;

    @ManyToOne
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    private NotificationStatus status;
}
