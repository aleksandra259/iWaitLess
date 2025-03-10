package com.iwaitless.persistence.entity.nomenclatures;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table(name = "HT_MENU_CATEGORY")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class MenuCategory extends AbstractNomenclatureEntity{
    @NotNull
    @Column(name = "ORDER_NO")
    private Integer orderNo;
}
