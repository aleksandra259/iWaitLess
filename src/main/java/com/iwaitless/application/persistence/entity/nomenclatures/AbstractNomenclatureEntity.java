package com.iwaitless.application.persistence.entity.nomenclatures;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractNomenclatureEntity {
    @Id
    private String id;

    @NotNull
    private String name;
}
