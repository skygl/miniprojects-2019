package com.wootecobook.turkey.messenger.service.dto;

import com.wootecobook.turkey.messenger.domain.MessengerRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MessengerRoomResponse {

    private Long id;

    public MessengerRoomResponse(final Long id) {
        this.id = id;
    }

    public static MessengerRoomResponse from(final MessengerRoom messengerRoom) {
        return new MessengerRoomResponse(messengerRoom.getId());
    }
}
