package com.wootecobook.turkey.user.domain;

import com.wootecobook.turkey.friend.domain.Friend;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 다섯명만_검색되는지_확인() {
        // given
        final String name = "name";
        final int size = 5;
        final Pageable pageable = PageRequest.of(0, size);

        userRepository.save(new User("UserRepoTest1@gmail.com", "A" + name, "P@ssw0rd"));
        userRepository.save(new User("UserRepoTest2@gmail.com", name + "A", "P@ssw0rd"));
        userRepository.save(new User("UserRepoTest3@gmail.com", "qwrqwr" + name, "P@ssw0rd"));
        userRepository.save(new User("UserRepoTest4@gmail.com", "qwrqwr" + name, "P@ssw0rd"));
        userRepository.save(new User("UserRepoTest5@gmail.com", "qwrqwr" + name, "P@ssw0rd"));
        userRepository.save(new User("UserRepoTest6@gmail.com", "qwrqwr" + name, "P@ssw0rd"));
        userRepository.save(new User("UserRepoTest7@gmail.com", "qwrqwr" + name, "P@ssw0rd"));

        // when
        final Page<User> users = userRepository.findAllByNameIsContaining(name, pageable);

        // then
        assertThat(users).hasSize(size);
    }

    @Test
    void 친구_2명이_조회되는지_테스트() {
        // given
        User firstUser = userRepository.save(new User("UserRepoTest1@gmail.com", "Aname", "P@ssw0rd"));
        User secondUser = userRepository.save(new User("UserRepoTest2@gmail.com", "nameA", "P@ssw0rd"));
        User thirdUser = userRepository.save(new User("UserRepoTest3@gmail.com", "qwrqwrname", "P@ssw0rd"));

        makeFriend(firstUser.getId(), secondUser.getId());
        makeFriend(firstUser.getId(), thirdUser.getId());

        // when
        List<User> friendsOfFirstUser = userRepository.findFriendsByUserId(firstUser.getId());

        // then
        assertThat(friendsOfFirstUser).hasSize(2);
        assertThat(friendsOfFirstUser).isEqualTo(Arrays.asList(secondUser, thirdUser));
    }

    private void makeFriend(Long userId, Long friendId) {
        testEntityManager.persist(Friend.builder()
                .relatedUserId(userId)
                .relatingUserId(friendId)
                .build());

        testEntityManager.persist(Friend.builder()
                .relatedUserId(friendId)
                .relatingUserId(userId)
                .build());
    }
}