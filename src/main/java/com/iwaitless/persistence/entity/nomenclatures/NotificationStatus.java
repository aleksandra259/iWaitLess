package com.iwaitless.persistence.entity.nomenclatures;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Table(name = "HT_NOTIFICATION_STATUS")
@Entity
@AllArgsConstructor
public class NotificationStatus extends AbstractNomenclatureEntity {
}
