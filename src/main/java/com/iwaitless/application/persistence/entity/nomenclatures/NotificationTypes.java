package com.iwaitless.application.persistence.entity.nomenclatures;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Table(name = "HT_NOTIFICATION_TYPES")
@Entity
@Getter
@Setter
@AllArgsConstructor
public class NotificationTypes {

    @Id
    private String id;

    @NotNull
    private String name;
}
