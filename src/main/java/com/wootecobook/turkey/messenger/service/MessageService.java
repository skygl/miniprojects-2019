package com.wootecobook.turkey.messenger.service;

import com.wootecobook.turkey.messenger.domain.Message;
import com.wootecobook.turkey.messenger.domain.MessageRepository;
import com.wootecobook.turkey.messenger.domain.Messenger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(final MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message save(final Messenger messenger, final String messageContents) {
        Message message = new Message(messenger.getMessengerRoom(), messenger.getUser(), messageContents);
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> findByMessengerRoomId(final Long roomId) {
        return Collections.unmodifiableList(messageRepository.findByMessengerRoomId(roomId));
    }
}
