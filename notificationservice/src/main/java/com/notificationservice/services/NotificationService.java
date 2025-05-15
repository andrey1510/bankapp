package com.notificationservice.services;

import com.notificationservice.dto.NotificationRequest;
import jakarta.transaction.Transactional;

public interface NotificationService {

    void processNotification(NotificationRequest request);

}
