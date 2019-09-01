package com.wootecobook.turkey.messenger.service.strategy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessengerRoomCodeStrategyConfig {

    @Bean
    MessengerRoomCodeStrategy plusJoinStrategy() {
        return new JoinStrategy("+");
    }
}
