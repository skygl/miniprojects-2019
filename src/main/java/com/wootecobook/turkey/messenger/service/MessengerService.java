package com.wootecobook.turkey.messenger.service;

import com.wootecobook.turkey.messenger.domain.Messenger;
import com.wootecobook.turkey.messenger.domain.MessengerRepository;
import com.wootecobook.turkey.messenger.domain.MessengerRoom;
import com.wootecobook.turkey.messenger.service.dto.MessageResponse;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRoomResponse;
import com.wootecobook.turkey.messenger.service.exception.AccessDeniedException;
import com.wootecobook.turkey.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Set;

@Service
@Transactional
public class MessengerService {

    private static final String NOT_FOUND_MESSAGE = "존재하지 않는 메신저입니다.";

    private final MessengerRepository messengerRepository;
    private final MessengerRoomService messengerRoomService;
    private final UserService userService;
    private final MessageService messageService;

    public MessengerService(MessengerRepository messengerRepository, MessengerRoomService messengerRoomService,
                            MessageService messageService, UserService userService) {
        this.messengerRepository = messengerRepository;
        this.messengerRoomService = messengerRoomService;
        this.userService = userService;
        this.messageService = messageService;
    }

    public MessengerRoomResponse findMessengerRoom(MessengerRequest messengerRequest, Long userId) {
        Set<Long> userIds = getMessengerUserIds(messengerRequest, userId);

        MessengerRoom messengerRoom = messengerRoomService.findByUserIds(userIds)
                .orElseGet(() -> createMessengerRoom(userIds));
        return MessengerRoomResponse.from(messengerRoom);
    }

    private Set<Long> getMessengerUserIds(MessengerRequest messengerRequest, Long userId) {
        messengerRequest.add(userId);
        return Collections.unmodifiableSet(messengerRequest.getUserIds());
    }

    private MessengerRoom createMessengerRoom(Set<Long> userIds) {
        MessengerRoom messengerRoom = messengerRoomService.save(userIds);
        userIds.stream()
                .map(userService::findById)
                .map(user -> new Messenger(messengerRoom, user))
                .forEach(messengerRepository::save);
        return messengerRoom;
    }

    @Transactional(readOnly = true)
    public void checkMember(Long roomId, Long userId) {
        if (messengerRepository.existsByUserIdAndMessengerRoomId(userId, roomId)) {
            return;
        }
        throw new AccessDeniedException();
    }

    public MessageResponse sendMessage(Long roomId, Long userId, String message) {
        Messenger messenger = findMessenger(userId, roomId);
        return MessageResponse.from(messageService.save(messenger, message));
    }

    private Messenger findMessenger(Long userId, Long messengerRoomId) {
        return messengerRepository.findByUserIdAndMessengerRoomId(userId, messengerRoomId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));
    }

}