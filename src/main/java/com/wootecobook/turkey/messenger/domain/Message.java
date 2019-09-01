package com.wootecobook.turkey.messenger.domain;

import com.wootecobook.turkey.commons.domain.BaseEntity;
import com.wootecobook.turkey.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messenger_room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_to_messenger_room"), updatable = false)
    private MessengerRoom messengerRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_to_sender"), updatable = false)
    private User sender;

    @Lob
    @Column(nullable = false)
    private String content;

    public Message(final MessengerRoom messengerRoom, final User sender, final String content) {
        this.messengerRoom = messengerRoom;
        this.sender = sender;
        this.content = content;
    }
}