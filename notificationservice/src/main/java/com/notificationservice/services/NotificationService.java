package com.notificationservice.services;

import com.notificationservice.dto.NotificationRequestDto;

public interface NotificationService {

    void processNotification(NotificationRequestDto request);

}
