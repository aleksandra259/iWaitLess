package com.iwaitless.application.persistence.entity.nomenclatures;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Table(name = "HT_NOTIFICATION_TYPE")
@Entity
@AllArgsConstructor
public class NotificationTypes extends AbstractNomenclatureEntity {
}
