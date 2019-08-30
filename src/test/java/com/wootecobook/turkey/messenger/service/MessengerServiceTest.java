package com.wootecobook.turkey.messenger.service;

import com.wootecobook.turkey.messenger.domain.Message;
import com.wootecobook.turkey.messenger.domain.Messenger;
import com.wootecobook.turkey.messenger.domain.MessengerRepository;
import com.wootecobook.turkey.messenger.domain.MessengerRoom;
import com.wootecobook.turkey.messenger.service.dto.MessageResponse;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    @Mock
    private MessageService messageService;
    private User user;
    private MessengerRoom messengerRoom;
    private String messageTxt;
    private Messenger messenger;

    @BeforeEach
    void setUp() {
        messengerRoom = new MessengerRoom("test");
        messengerRoom.setId(1L);
        user = new User("test@mail.com", "name", "Passw0rd!");
        user.setId(1L);
        messenger = new Messenger(messengerRoom, user);
        messageTxt = "contents";
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

    @Test
    void 메세지_전송_성공_테스트() {
        //given
        Message message = new Message(messengerRoom, user, messageTxt);
        when(messengerRepository.findByUserIdAndMessengerRoomId(any(), any())).thenReturn(Optional.of(messenger));
        when(messageService.save(any(), any())).thenReturn(message);
        //when
        MessageResponse messageResponse = messengerService.sendMessage(messengerRoom.getId(), user.getId(), messageTxt);
        //then
        assertThat(messageResponse.getSender().getId()).isEqualTo(user.getId());
        assertThat(messageResponse.getContent()).isEqualTo(messageTxt);
    }

    @Test
    void 메세지_전송_실패_테스트() {
        //given
        when(messengerRepository.findByUserIdAndMessengerRoomId(any(), any())).thenReturn(Optional.empty());
        //when & then
        assertThrows(EntityNotFoundException.class, () ->
                messengerService.sendMessage(1L, user.getId(), messageTxt));
    }

    @Test
    void 메신저룸_전체_메세지_조회_테스트() {
        //given
        List<Message> messages = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> new Message(messengerRoom, user, "message" + i))
                .collect(Collectors.toList());
        when(messageService.findByMessengerRoomId(messengerRoom.getId())).thenReturn(messages);
        //when
        List<MessageResponse> messageResponses = messengerService.findMessageResponsesByRoomId(messengerRoom.getId());
        //then
        assertThat(messages.size()).isEqualTo(messageResponses.size());
        IntStream.rangeClosed(0, 4)
                .forEach(i -> matchMessageAndResponse(messages.get(i), messageResponses.get(i)));
    }

    private void matchMessageAndResponse(Message message, MessageResponse messageResponse) {
        assertThat(message.getContent()).isEqualTo(messageResponse.getContent());
    }
}