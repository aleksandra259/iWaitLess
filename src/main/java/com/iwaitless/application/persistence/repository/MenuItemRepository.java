package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.MenuItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItems, Long> {

    @Query("select c from MenuItems c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(c.description) like lower(concat('%', :searchTerm, '%'))")
    List<MenuItems> search(@Param("searchTerm") String searchTerm);
}
