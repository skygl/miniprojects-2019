package com.wootecobook.turkey.messenger.domain;

import com.wootecobook.turkey.commons.domain.UpdatableEntity;
import com.wootecobook.turkey.messenger.domain.exception.InvalidMessengerRoomException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessengerRoom extends UpdatableEntity {

    @OneToMany(mappedBy = "messengerRoom")
    private List<Messenger> users = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String code;

    public MessengerRoom(final String code) {
        if (code == null || code.isEmpty()) {
            throw new InvalidMessengerRoomException();
        }
        this.code = code;
    }
}
