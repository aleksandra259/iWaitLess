package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationTypes;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "NOTIFICATIONS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "NOTIFICATION_SEQ")
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "ID")
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
