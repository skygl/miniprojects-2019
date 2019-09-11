package com.wootecobook.turkey.notification;

import com.wootecobook.turkey.user.domain.User;

public enum NotificationType {

    POST_RECEIVED(((sender, receiver) ->
            sender.getName() + " 님이 " + receiver.getName() + " 님에게 글을 작성했습니다")),
    POST_TAGGED(((sender, receiver) ->
            receiver.getName() + " 님이 " + sender.getName() + " 님의 글에 태그되었습니다"));

    private NotificationMessageGenerator notificationMessageGenerator;

    NotificationType(NotificationMessageGenerator notificationMessageGenerator) {
        this.notificationMessageGenerator = notificationMessageGenerator;
    }

    public String generateNotificationMessage(User sender, User receiver) {
        return notificationMessageGenerator.generateNotificationMessage(sender, receiver);
    }

}
