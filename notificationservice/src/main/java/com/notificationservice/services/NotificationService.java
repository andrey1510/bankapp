package com.notificationservice.services;

import com.notificationservice.dto.kafka.NotificationRequestDto;

public interface NotificationService {

    void processNotification(NotificationRequestDto request);

}
