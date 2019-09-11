package com.wootecobook.turkey.notification;

import com.wootecobook.turkey.user.domain.User;

@FunctionalInterface
public interface NotificationMessageGenerator {

    String generateNotificationMessage(User sender, User receiver);

}
