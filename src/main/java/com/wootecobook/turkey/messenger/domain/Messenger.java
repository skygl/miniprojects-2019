package com.wootecobook.turkey.messenger.domain;

import com.wootecobook.turkey.commons.domain.BaseEntity;
import com.wootecobook.turkey.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Messenger extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "messenger_room_id", nullable = false)
    private MessengerRoom messengerRoom;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Messenger(final MessengerRoom messengerRoom, final User user) {
        this.messengerRoom = messengerRoom;
        this.user = user;
    }
}
