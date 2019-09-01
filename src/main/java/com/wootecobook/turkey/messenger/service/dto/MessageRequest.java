package com.wootecobook.turkey.messenger.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequest {
    private String message;

    public MessageRequest(String message) {
        this.message = message;
    }
}
