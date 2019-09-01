package com.wootecobook.turkey.messenger.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessengerRepository extends JpaRepository<Messenger, Long> {

    boolean existsByUserIdAndMessengerRoomId(Long userId, Long messengerRoomId);

    Optional<Messenger> findByUserIdAndMessengerRoomId(Long userId, Long messengerRoomId);
}
