package com.notificationservice.repositories;

import com.notificationservice.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByIsSentFalse();

}
