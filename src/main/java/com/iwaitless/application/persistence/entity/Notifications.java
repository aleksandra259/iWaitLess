package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

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
    @Column(name = "NOTIF_ID", updatable = false, nullable = false, insertable = false, unique = true)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Staff employee;

    @ManyToOne
    @JoinColumn(name = "TYPE", referencedColumnName = "ID")
    private NotificationTypes type;

    @ManyToOne
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    private NotificationStatus status;

    @ManyToOne
    @JoinColumn(name = "TABLE_ID")
    private RestaurantTable table;

    @ManyToOne
    @JoinColumn(name = "ORDER_NO")
    private Orders order;

    @NotNull
    @Column(name = "REGISTRATION_DATE")
    private Timestamp registrationDate;
}
