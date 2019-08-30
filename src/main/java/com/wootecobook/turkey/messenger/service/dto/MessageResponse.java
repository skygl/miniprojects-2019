package com.wootecobook.turkey.messenger.service.dto;

import com.wootecobook.turkey.messenger.domain.Message;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MessageResponse {

    private String content;
    private UserResponse sender;

    private MessageResponse(final String content, final UserResponse sender) {
        this.content = content;
        this.sender = sender;
    }

    public static MessageResponse from(final Message message) {
        UserResponse sender = UserResponse.from(message.getSender());
        return new MessageResponse(message.getContent(), sender);
    }
}

