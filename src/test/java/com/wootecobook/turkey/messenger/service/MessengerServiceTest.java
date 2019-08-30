package com.wootecobook.turkey.messenger.service;

import com.wootecobook.turkey.messenger.domain.Messenger;
import com.wootecobook.turkey.messenger.domain.MessengerRepository;
import com.wootecobook.turkey.messenger.domain.MessengerRoom;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRoomResponse;
import com.wootecobook.turkey.messenger.service.exception.AccessDeniedException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MessengerServiceTest {

    @InjectMocks
    private MessengerService messengerService;
    @Mock
    private MessengerRepository messengerRepository;
    @Mock
    private MessengerRoomService messengerRoomService;
    @Mock
    private UserService userService;
    private User user;
    private MessengerRoom messengerRoom;

    @BeforeEach
    void setUp() {
        messengerRoom = new MessengerRoom("test");
        messengerRoom.setId(1L);
        user = new User("test@mail.com", "name", "Passw0rd!");
        user.setId(1L);
    }

    @Test
    void 새로운방_생성_경우_테스트() {
        //given
        when(messengerRoomService.findByUserIds(any())).thenReturn(Optional.empty());
        when(messengerRoomService.save(any())).thenReturn(messengerRoom);
        when(userService.findById(user.getId())).thenReturn(user);
        //when
        MessengerRoomResponse messengerRoomResponse = messengerService.findMessengerRoom(new MessengerRequest(), user.getId());
        //then
        assertThat(messengerRoomResponse.getId()).isEqualTo(messengerRoom.getId());
    }

    @Test
    void 메신저룸_이미_존재하던_경우_테스트() {
        //given
        when(messengerRoomService.findByUserIds(any())).thenReturn(Optional.of(messengerRoom));
        //when
        MessengerRoomResponse messengerRoomResponse = messengerService.findMessengerRoom(new MessengerRequest(), 1L);
        //then
        assertThat(messengerRoomResponse.getId()).isEqualTo(messengerRoom.getId());
    }

    @Test
    void 메신저룸_멤버인_경우() {
        //given
        when(messengerRepository.existsByUserIdAndMessengerRoomId(any(), any())).thenReturn(true);
        //when & then
        assertDoesNotThrow(() -> messengerService.checkMember(1L, 1L));
    }

    @Test
    void 메신저룸_멤버가_아닌_경우() {
        //given
        when(messengerRepository.existsByUserIdAndMessengerRoomId(any(), any())).thenReturn(false);
        //when & then
        assertThrows(AccessDeniedException.class, () -> messengerService.checkMember(1L, 1L));
    }
}