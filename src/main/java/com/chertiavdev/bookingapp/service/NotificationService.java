package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.model.Role.RoleName;

public interface NotificationService {
    void sendNotification(String message, RoleName targetRole);

    void sendNotificationByUserId(String message, Long userId);
}
