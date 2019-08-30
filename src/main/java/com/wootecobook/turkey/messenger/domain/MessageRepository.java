package com.wootecobook.turkey.messenger.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByMessengerRoomId(Long messengerRoomId);

}
