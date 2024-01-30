package com.iwaitless.application.persistence.entity;

import com.iwaitless.application.persistence.entity.nomenclatures.NotificationStatus;
import com.iwaitless.application.persistence.entity.nomenclatures.NotificationTypes;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table(name = "NOTIFICATIONS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Notifications {

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
