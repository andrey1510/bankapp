package com.notificationservice.services;

import com.notificationservice.dto.NotificationRequest;
import com.notificationservice.entities.Notification;
import com.notificationservice.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Transactional
    @Override
    public void processNotification(NotificationRequest request) {

        Notification notification = Notification.builder()
            .email(request.email())
            .message(request.message())
            .isSent(false)
            .build();
        notification = notificationRepository.save(notification);

        try {
            sendEmail(request.email(), request.message());
            notification.setIsSent(true);
            notificationRepository.save(notification);
        } catch (MailException e) {
            log.error(e.getMessage());
        }
    }

    private void sendEmail(String email, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Уведомление от банковского приложения");
        message.setText(text);
        mailSender.send(message);
    }

}
