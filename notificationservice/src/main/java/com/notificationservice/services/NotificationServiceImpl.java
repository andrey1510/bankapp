package com.notificationservice.services;

import com.notificationservice.dto.kafka.NotificationRequestDto;
import com.notificationservice.entities.Notification;
import com.notificationservice.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Transactional
    @Override
    public void processNotification(NotificationRequestDto request) {

        Notification notification = Notification.builder()
            .email(request.getEmail())
            .message(request.getMessage())
            .isSent(false)
            .build();
        notification = notificationRepository.save(notification);

        try {
            sendEmail(request.getEmail(), request.getMessage());
            notification.setIsSent(true);
            notificationRepository.save(notification);
        } catch (MailException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void retryFailedNotifications() {

        List<Notification> failedNotifications = notificationRepository.findByIsSentFalse();

        for (Notification notification : failedNotifications) {
            try {
                sendEmail(notification.getEmail(), notification.getMessage());
                notification.setIsSent(true);
            } catch (MailException e) {
                log.error("Failed to resend to {}: {}", notification.getEmail(), e.getMessage());
            }
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
