package com.wootecobook.turkey.messenger.service;

import com.wootecobook.turkey.messenger.domain.MessengerRoom;
import com.wootecobook.turkey.messenger.domain.MessengerRoomRepository;
import com.wootecobook.turkey.messenger.service.strategy.MessengerRoomCodeStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MessengerRoomServiceTest {

    @InjectMocks
    private MessengerRoomService messengerRoomService;
    @Mock
    private MessengerRoomRepository messengerRoomRepository;
    @Mock
    private MessengerRoomCodeStrategy testRoomCodeStrategy;

    @Test
    void 메신저룸_저장_테스트() {
        //given
        String roomCode = "testRoomCode";
        when(testRoomCodeStrategy.createCode(any(HashSet.class))).thenReturn(roomCode);
        when(messengerRoomRepository.save(any(MessengerRoom.class))).then(returnsFirstArg());
        //when
        MessengerRoom messengerRoom = messengerRoomService.save(new HashSet<>());
        //then
        assertThat(messengerRoom.getCode()).isEqualTo(roomCode);
    }

    @Test
    void 유저로_메신저룸_찾기_테스트() {
        //given
        String roomCode = "testRoomCode";
        Set<Long> userIds = new HashSet<>(Arrays.asList(1L, 2L));
        when(testRoomCodeStrategy.createCode(userIds)).thenReturn(roomCode);
        when(messengerRoomRepository.findByCode(roomCode)).thenReturn(Optional.of(new MessengerRoom(roomCode)));
        //when
        MessengerRoom room = messengerRoomService.findByUserIds(userIds).get();
        //then
        assertThat(room.getCode()).isEqualTo(roomCode);
    }

}