package com.wootecobook.turkey.messenger.service.strategy;

import java.util.Set;
import java.util.stream.Collectors;

public class JoinStrategy implements MessengerRoomCodeStrategy {

    private final String delimiter;

    public JoinStrategy(final String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String createCode(final Set<Long> ids) {
        return ids.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }
}
