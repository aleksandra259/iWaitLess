package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

}
