package com.wootecobook.turkey.post.domain;

import com.wootecobook.turkey.post.domain.exception.InvalidPostException;
import com.wootecobook.turkey.post.domain.exception.PostUpdateFailException;
import com.wootecobook.turkey.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostTest {

    private Contents testContents;
    private User author;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .name("olaf")
                .email("olaf@woowa.com")
                .password("passw0rD!")
                .build();

        testContents = new Contents("hello");
    }

    @Test
    void 생성() {
        Post post = Post.builder()
                .contents(testContents)
                .author(author)
                .build();

        assertThat(post.getContents()).isEqualTo(testContents);
    }

    @Test
    void 내용이_없는_경우_예외_테스트() {
        assertThrows(InvalidPostException.class, () -> Post.builder()
                .author(author)
                .build()
        );
    }

    @Test
    void 수정시_Post가_null인_경우_예외_테스트() {
        Post post = Post.builder()
                .contents(testContents)
                .author(author)
                .build();

        assertThrows(PostUpdateFailException.class, () -> post.update(null));
    }

    @Test
    void 유저가_없는_경우_예외_테스트() {
        assertThrows(InvalidPostException.class, () -> Post.builder()
                .contents(testContents)
                .build()
        );
    }

    @Test
    void 내용과_유저가_없는_경우_예외_테스트() {
        assertThrows(InvalidPostException.class, () -> Post.builder().build());
    }
}
