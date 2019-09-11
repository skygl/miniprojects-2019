package com.wootecobook.turkey.notification.service;

import com.wootecobook.turkey.commons.firebase.FCMService;
import com.wootecobook.turkey.notification.service.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final FCMService fcmService;

    private final Map<Long, String> tokenMap = new HashMap<>();

    public NotificationService(final FCMService fcmService) {
        this.fcmService = fcmService;
    }

    public void register(final Long userId, final String token) {
        tokenMap.put(userId, token);
    }

    public void deleteToken(final Long userId) {
        tokenMap.remove(userId);
    }

    public String getToken(final Long userId) {
        return tokenMap.get(userId);
    }

    public void sendNotification(final NotificationRequest request) {
        try {
            fcmService.send(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

}