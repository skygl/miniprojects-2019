package com.wootecobook.turkey.friend.service;

import com.wootecobook.turkey.friend.domain.Friend;
import com.wootecobook.turkey.friend.domain.FriendAskRepository;
import com.wootecobook.turkey.friend.domain.FriendRepository;
import com.wootecobook.turkey.friend.service.dto.FriendAskCreate;
import com.wootecobook.turkey.friend.service.dto.FriendCreate;
import com.wootecobook.turkey.friend.service.exception.AlreadyFriendException;
import com.wootecobook.turkey.friend.service.exception.MismatchedUserException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class FriendServiceTest {

    private static final String SENDER_NAME = "sender";
    private static final String SENDER_EMAIL = "sender@abc.abc";
    private static final String RECEIVER_NAME = "receiver";
    private static final String RECEIVER_EMAIL = "receiver@abc.abc";
    private static final String VALID_PASSWORD = "P@ssw0rd";

    private UserService userService;
    private FriendService friendService;
    private FriendAskService friendAskService;

    private Long senderId;
    private Long receiverId;
    private Long friendAskId;

    @Autowired
    public FriendServiceTest(FriendRepository friendRepository, UserRepository userRepository,
                             FriendAskRepository friendAskRepository) {
        userService = new UserService(userRepository);
        friendAskService = new FriendAskService(friendAskRepository, userService);
        friendService = new FriendService(friendRepository, friendAskService, userService);
    }

    @BeforeEach
    void setUp() {
        UserRequest senderRequest = UserRequest.builder()
                .email(SENDER_EMAIL)
                .name(SENDER_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserRequest receiverRequest = UserRequest.builder()
                .email(RECEIVER_EMAIL)
                .name(RECEIVER_NAME)
                .password(VALID_PASSWORD)
                .build();

        User sender = userService.save(senderRequest);
        User receiver = userService.save(receiverRequest);

        senderId = sender.getId();
        receiverId = receiver.getId();

        FriendAskCreate friendAskCreate = FriendAskCreate.builder().receiverId(receiverId).build();
        friendAskId = friendAskService.save(senderId, friendAskCreate).getFriendAskId();
    }

    @Test
    void 친구_요청_수락() {
        //given
        FriendCreate friendCreate = FriendCreate.builder()
                .friendAskId(friendAskId)
                .build();

        //when
        List<Friend> friends = friendService.save(friendCreate);

        //then
        assertThat(friends.size()).isEqualTo(2);

        Friend friend = friends.get(0);
        Friend reverseFriend = friends.get(1);
        assertThat(friend.getRelatingUserId()).isEqualTo(senderId);
        assertThat(friend.getRelatedUserId()).isEqualTo(receiverId);
        assertThat(reverseFriend.getRelatingUserId()).isEqualTo(receiverId);
        assertThat(reverseFriend.getRelatedUserId()).isEqualTo(senderId);
    }

    @Test
    void 친구_요청_수락시_이미_친구인_경우() {
        //given
        FriendCreate friendCreate = FriendCreate.builder()
                .friendAskId(friendAskId)
                .build();

        friendService.save(friendCreate);

        //when & then
        assertThrows(AlreadyFriendException.class, () -> friendService.checkAlreadyFriend(senderId, receiverId));
    }

    @Test
    void 친구_삭제() {
        //given
        Long friendId = createFriend();

        //when & then
        assertDoesNotThrow(() -> friendService.deleteById(friendId, receiverId));
    }

    @Test
    void 잘못된_유저가_친구_삭제() {
        //given
        Long friendId = createFriend();

        //when & then
        assertThrows(MismatchedUserException.class,
                () -> friendService.deleteById(friendId, Long.MAX_VALUE));
    }

    @Test
    void 잘못된_친구_Id_삭제() {
        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> friendService.deleteById(Long.MAX_VALUE, receiverId));
    }

    private Long createFriend() {
        FriendCreate friendCreate = FriendCreate.builder()
                .friendAskId(friendAskId)
                .build();

        List<Friend> friends = friendService.save(friendCreate);

        return friends.get(1).getId();
    }
}
