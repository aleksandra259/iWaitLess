package com.iwaitless.persistence.repository;


import com.iwaitless.persistence.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

}
