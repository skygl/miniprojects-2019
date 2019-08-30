package com.wootecobook.turkey.messenger.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessengerRepository extends JpaRepository<Messenger, Long> {

    boolean existsByUserIdAndMessengerRoomId(Long userId, Long messengerRoomId);
}
