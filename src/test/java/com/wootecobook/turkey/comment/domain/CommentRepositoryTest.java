package com.wootecobook.turkey.comment.domain;

import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.post.domain.PostRepository;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private Post post;
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("PostRepoTest@gmail.com", "name", "P@ssw0rd"));
        post = Post.builder()
                .contents(new Contents("contents"))
                .author(user)
                .build();
        post = postRepository.save(post);

        comment = new Comment("contents1", user, post, null);

        // 댓글 작성
        commentRepository.save(comment);
        commentRepository.save(new Comment("contents2", user, post, null));
        commentRepository.save(new Comment("contents3", user, post, null));

        // 답글 작성
        commentRepository.save(new Comment("childComment1", user, post, comment));
        commentRepository.save(new Comment("childComment2", user, post, comment));
        commentRepository.save(new Comment("childComment3", user, post, comment));
    }

    @Test
    void 답글을_제외한_댓글만_조회() {
        // given
        final int size = 10;
        final Pageable pageable = PageRequest.of(0, size);

        // when
        final Page<Comment> comments = commentRepository.findAllByPostIdAndParentIdIsNull(post.getId(), pageable);
        final long actualSize = comments
                .stream()
                .filter(co -> !co.getParent().isPresent())
                .count();

        // then
        assertThat(actualSize).isEqualTo(comments.getTotalElements());
    }

    @Test
    void 답글만_조회() {
        // given
        final int size = 10;
        final Pageable pageable = PageRequest.of(0, size);

        // when
        final Page<Comment> comments = commentRepository.findAllByParentId(comment.getId(), pageable);
        final long actualSize = comments
                .stream()
                .filter(co -> co.getParent().isPresent())
                .count();

        // then
        assertThat(actualSize).isEqualTo(comments.getTotalElements());
    }
}