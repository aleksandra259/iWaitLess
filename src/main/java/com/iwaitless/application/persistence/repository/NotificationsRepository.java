package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

}
