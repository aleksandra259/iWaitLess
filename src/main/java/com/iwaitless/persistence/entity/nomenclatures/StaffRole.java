package com.iwaitless.persistence.entity.nomenclatures;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Table(name = "HT_STAFF_ROLE")
@Entity
@AllArgsConstructor
public class StaffRole extends AbstractNomenclatureEntity {
}

