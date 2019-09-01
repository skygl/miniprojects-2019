package com.wootecobook.turkey.messenger.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessengerRoomRepository extends JpaRepository<MessengerRoom, Long> {

    Optional<MessengerRoom> findByCode(String code);
}
