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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessengerService {

    private static final String NOT_FOUND_MESSAGE = "존재하지 않는 메신저입니다.";

    private final MessengerRepository messengerRepository;
    private final MessengerRoomService messengerRoomService;
    private final UserService userService;
    private final MessageService messageService;

    public MessengerService(final MessengerRepository messengerRepository,
                            final MessengerRoomService messengerRoomService,
                            final MessageService messageService,
                            final UserService userService) {
        this.messengerRepository = messengerRepository;
        this.messengerRoomService = messengerRoomService;
        this.userService = userService;
        this.messageService = messageService;
    }

    public MessengerRoomResponse findMessengerRoom(final MessengerRequest messengerRequest, final Long userId) {
        Set<Long> userIds = getMessengerUserIds(messengerRequest, userId);

        MessengerRoom messengerRoom = messengerRoomService.findByUserIds(userIds)
                .orElseGet(() -> createMessengerRoom(userIds));
        return MessengerRoomResponse.from(messengerRoom);
    }

    private Set<Long> getMessengerUserIds(final MessengerRequest messengerRequest, final Long userId) {
        messengerRequest.add(userId);
        return Collections.unmodifiableSet(messengerRequest.getUserIds());
    }

    private MessengerRoom createMessengerRoom(final Set<Long> userIds) {
        MessengerRoom messengerRoom = messengerRoomService.save(userIds);
        List<Messenger> messengers = userIds.stream()
                .map(userService::findById)
                .map(user -> new Messenger(messengerRoom, user))
                .collect(Collectors.toList());
        messengerRepository.saveAll(messengers);
        return messengerRoom;
    }

    @Transactional(readOnly = true)
    public void checkMember(final Long roomId, final Long userId) {
        if (messengerRepository.existsByUserIdAndMessengerRoomId(userId, roomId)) {
            return;
        }
        throw new AccessDeniedException();
    }

    public MessageResponse sendMessage(final Long roomId, final Long userId, final String message) {
        Messenger messenger = findMessenger(userId, roomId);
        return MessageResponse.from(messageService.save(messenger, message));
    }

    private Messenger findMessenger(final Long userId, final Long messengerRoomId) {
        return messengerRepository.findByUserIdAndMessengerRoomId(userId, messengerRoomId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> findMessageResponsesByRoomId(final Long roomId) {
        return messageService.findByMessengerRoomId(roomId).stream()
                .map(MessageResponse::from)
                .collect(Collectors.toList());
    }

}