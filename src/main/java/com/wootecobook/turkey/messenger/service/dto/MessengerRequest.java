package com.wootecobook.turkey.messenger.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MessengerRequest {
    private Set<Long> userIds = new HashSet<>();

    public MessengerRequest(Set<Long> userIds) {
        this.userIds = userIds;
    }

    public void add(Long userId) {
        userIds.add(userId);
    }
}
