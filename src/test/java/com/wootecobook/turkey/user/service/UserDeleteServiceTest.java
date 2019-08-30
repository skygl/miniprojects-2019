package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.friend.domain.Friend;
import com.wootecobook.turkey.friend.domain.FriendAskRepository;
import com.wootecobook.turkey.friend.domain.FriendRepository;
import com.wootecobook.turkey.friend.service.FriendAskService;
import com.wootecobook.turkey.friend.service.FriendService;
import com.wootecobook.turkey.friend.service.dto.FriendAskCreate;
import com.wootecobook.turkey.friend.service.dto.FriendCreate;
import com.wootecobook.turkey.user.domain.IntroductionRepository;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.exception.UserDeleteException;
import com.wootecobook.turkey.user.service.exception.UserMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserDeleteServiceTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";
    private static final String SENDER_NAME = "sender";
    private static final String SENDER_EMAIL = "sender@abc.abc";
    private static final String RECEIVER_NAME = "receiver";
    private static final String RECEIVER_EMAIL = "receiver@abc.abc";

    private UserDeleteService userDeleteService;
    private UserService userService;
    private FriendAskService friendAskService;
    private FriendService friendService;
    private IntroductionService introductionService;

    private Long senderId;
    private Long receiverId;

    @Autowired
    public UserDeleteServiceTest(UserRepository userRepository, FriendAskRepository friendAskRepository,
                                 FriendRepository friendRepository, IntroductionRepository introductionRepository) {
        userService = new UserService(userRepository);
        friendAskService = new FriendAskService(friendAskRepository, userService);
        friendService = new FriendService(friendRepository, friendAskService, userService);
        introductionService = new IntroductionService(introductionRepository, userService);
        userDeleteService = new UserDeleteService(userService, friendAskService, friendService, introductionService);
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

        User senderResponse = userService.save(senderRequest);
        User receiverResponse = userService.save(receiverRequest);

        senderId = senderResponse.getId();
        receiverId = receiverResponse.getId();

        introductionService.save(senderId);
        introductionService.save(receiverId);
    }

    @Test
    void 유저_삭제() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();
        User user = userService.save(userRequest);
        introductionService.save(user.getId());

        //when & then
        assertDoesNotThrow(() -> userDeleteService.delete(user.getId(), user.getId()));
        assertThrows(EntityNotFoundException.class, () -> introductionService.findByUserId(user.getId()));
    }

    @Test
    void 없는_유저_삭제() {
        //when & then
        assertThrows(UserDeleteException.class, () -> userDeleteService.delete(Long.MAX_VALUE, Long.MAX_VALUE));
    }

    @Test
    void null_유저_삭제() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        User user = userService.save(userRequest);

        //when & then
        assertThrows(UserMismatchException.class, () -> userDeleteService.delete(null, user.getId()));
    }

    @Test
    void 다른_id_유저_삭제() {
        //when & then
        assertThrows(UserMismatchException.class, () -> userDeleteService.delete(1L, 2L));
    }

    @Test
    void 유저_삭제시_친구_요청_삭제() {
        //given
        Long friendAskId = createFriendAsk();

        //when
        userDeleteService.delete(senderId, senderId);

        //then
        assertThrows(EntityNotFoundException.class, () -> friendAskService.findById(friendAskId));
    }

    @Test
    void 유저_삭제시_친구_삭제() {
        //given
        Long friendAskId = createFriendAsk();
        Long friendId = createFriend(friendAskId);

        //when
        userDeleteService.delete(senderId, senderId);

        //then
        assertThrows(EntityNotFoundException.class, () -> friendService.findById(friendId));
    }

    private Long createFriendAsk() {
        FriendAskCreate friendAskCreate = FriendAskCreate.builder()
                .receiverId(receiverId)
                .build();
        return friendAskService.save(senderId, friendAskCreate).getFriendAskId();
    }

    private Long createFriend(Long friendAskId) {
        FriendCreate friendCreate = FriendCreate.builder()
                .friendAskId(friendAskId)
                .build();

        List<Friend> friends = friendService.save(friendCreate);

        return friends.get(1).getId();
    }
}
