package com.wootecobook.turkey.messenger.service;

import com.wootecobook.turkey.messenger.domain.Message;
import com.wootecobook.turkey.messenger.domain.MessageRepository;
import com.wootecobook.turkey.messenger.domain.Messenger;
import com.wootecobook.turkey.messenger.domain.MessengerRoom;
import com.wootecobook.turkey.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;
    @Mock
    private MessageRepository messageRepository;

    private MessengerRoom messengerRoom;
    private User sender;
    private Messenger messenger;

    @BeforeEach
    void setUp() {
        messengerRoom = new MessengerRoom("test");
        sender = new User("test@mail.com", "name", "Passw0rd!");
        sender.setId(1L);
        messenger = new Messenger(messengerRoom, sender);
    }

    @Test
    void 메세지_저장_테스트() {
        //given
        when(messageRepository.save(any(Message.class))).then(returnsFirstArg());
        String message = "test message";
        //when
        Message savedMessage = messageService.save(messenger, message);
        //then
        assertThat(savedMessage.getContent()).isEqualTo(message);
        assertThat(savedMessage.getSender().getId()).isEqualTo(sender.getId());
    }

    @Test
    void 메세지룸으로_메세지조회_테스트() {
        //given
        List<Message> messages = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> new Message(messengerRoom, sender, "message" + i))
                .collect(Collectors.toList());
        when(messageRepository.findByMessengerRoomId(1L)).thenReturn(messages);
        //when
        List<Message> savedMessages = messageService.findByMessengerRoomId(1L);
        //then
        assertThat(messages.size()).isEqualTo(savedMessages.size());
        IntStream.rangeClosed(0, 4)
                .forEach(i -> matchMessageAndResponse(messages.get(i), savedMessages.get(i)));
    }

    private void matchMessageAndResponse(Message message, Message savedMessage) {
        assertThat(message.getContent()).isEqualTo(savedMessage.getContent());
    }

}