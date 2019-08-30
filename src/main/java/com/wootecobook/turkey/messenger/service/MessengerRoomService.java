package com.wootecobook.turkey.messenger.service;

import com.wootecobook.turkey.messenger.domain.MessengerRoom;
import com.wootecobook.turkey.messenger.domain.MessengerRoomRepository;
import com.wootecobook.turkey.messenger.service.strategy.MessengerRoomCodeStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class MessengerRoomService {

    private final MessengerRoomRepository messengerRoomRepository;
    private final MessengerRoomCodeStrategy messengerRoomCodeStrategy;

    public MessengerRoomService(final MessengerRoomRepository messengerRoomRepository,
                                final MessengerRoomCodeStrategy plusJoinStrategy) {
        this.messengerRoomRepository = messengerRoomRepository;
        this.messengerRoomCodeStrategy = plusJoinStrategy;
    }

    public MessengerRoom save(final Set<Long> userIds) {
        MessengerRoom messengerRoom = new MessengerRoom(messengerRoomCodeStrategy.createCode(userIds));
        return messengerRoomRepository.save(messengerRoom);
    }

    @Transactional(readOnly = true)
    public Optional<MessengerRoom> findByUserIds(final Set<Long> userIds) {
        return messengerRoomRepository.findByCode(messengerRoomCodeStrategy.createCode(userIds));
    }

}
