package com.wootecobook.turkey.messenger.service.strategy;

import java.util.Set;

public interface MessengerRoomCodeStrategy {

    String createCode(Set<Long> userIds);
}
