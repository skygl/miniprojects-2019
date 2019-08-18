package com.wootecobook.turkey.comment.service;

import com.wootecobook.turkey.comment.domain.Comment;
import com.wootecobook.turkey.comment.domain.CommentRepository;
import com.wootecobook.turkey.comment.service.dto.CommentCreate;
import com.wootecobook.turkey.comment.service.dto.CommentResponse;
import com.wootecobook.turkey.comment.service.dto.CommentUpdate;
import com.wootecobook.turkey.comment.service.exception.AlreadyDeleteException;
import com.wootecobook.turkey.comment.service.exception.CommentNotFoundException;
import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.post.service.PostService;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private static final long POST_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long COMMENT_ID = 1L;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;
    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new User("CommentServiceTest@gamil.com", "name", "P@ssw0rd");
        user.setId(USER_ID);

        post = Post.builder()
                .id(POST_ID)
                .author(user)
                .contents(new Contents("contents"))
                .build();

        comment = new Comment("contents", user, post, null);
    }

    @Test
    void 댓글_저장() {
        // given
        final CommentCreate commentCreate = new CommentCreate("댓글 저장", null);
        final Comment comment = commentCreate.toEntity(user, post, null);

        when(userService.findById(USER_ID)).thenReturn(user);
        when(postService.findById(POST_ID)).thenReturn(post);
        when(commentRepository.save(any())).thenReturn(comment);

        // when
        final CommentResponse expected = commentService.save(commentCreate, USER_ID, POST_ID);

        // then
        verify(userService).findById(USER_ID);
        verify(postService).findById(POST_ID);
        verify(commentRepository).save(any());
        assertThat(commentCreate.getContents()).isEqualTo(expected.getContents());
    }

    @Test
    void 댓글_수정() {
        // given
        final CommentUpdate commentUpdate = new CommentUpdate("댓글_수정");
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));

        // when
        final CommentResponse commentResponse = commentService.update(commentUpdate, COMMENT_ID, USER_ID);

        // then
        verify(commentRepository).findById(COMMENT_ID);
        assertThat(commentUpdate.getContents()).isEqualTo(commentResponse.getContents());
    }

    @Test
    void 삭제된_댓글_수정시도_예외처리() {
        // given
        final CommentUpdate commentUpdate = new CommentUpdate("댓글_수정");
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));
        commentService.delete(COMMENT_ID, USER_ID);

        // when & then
        assertThrows(AlreadyDeleteException.class, () -> commentService.update(commentUpdate, COMMENT_ID, USER_ID));
    }

    @Test
    void 댓글_삭제() {
        // given
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));

        // when
        commentService.delete(COMMENT_ID, USER_ID);

        // then
        verify(commentRepository).findById(COMMENT_ID);
        assertThat(comment.isDeleted()).isTrue();
    }

    @Test
    void 삭제된_댓글_삭제시도_예외처리() {
        // given
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));
        commentService.delete(COMMENT_ID, USER_ID);

        // when & then
        assertThrows(AlreadyDeleteException.class, () -> commentService.delete(COMMENT_ID, USER_ID));
    }

    @Test
    void 존재하는_댓글_조회() {
        // given
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));

        // when
        final Comment expected = commentService.findById(COMMENT_ID);

        // then
        verify(commentRepository).findById(COMMENT_ID);
        assertThat(comment).isEqualTo(expected);
    }

    @Test
    void 존재하지_않는_댓글_조회_예외처리() {
        // given
        final Long commentId = 2101725L;
        when(commentRepository.findById(commentId)).thenThrow(CommentNotFoundException.class);

        // when & then
        assertThrows(CommentNotFoundException.class, () -> commentService.findById(commentId));
        verify(commentRepository).findById(commentId);
    }

    @Test
    void 댓글목록_조회_테스트() {
        // given
        final Page page = mock(Page.class);
        final Pageable pageable = mock(Pageable.class);

        when(commentRepository.findAllByPostIdAndParentIdIsNull(POST_ID, pageable)).thenReturn(page);
        when(page.map(any())).thenReturn(page);

        // when
        commentService.findCommentResponsesByPostId(POST_ID, pageable);

        // then
        verify(commentRepository).findAllByPostIdAndParentIdIsNull(POST_ID, pageable);
    }

    @Test
    void 답글목록_조회_테스트() {
        // given
        final Page page = mock(Page.class);
        final Pageable pageable = mock(Pageable.class);

        when(commentRepository.findAllByParentId(COMMENT_ID, pageable)).thenReturn(page);
        when(page.map(any())).thenReturn(page);

        // when
        commentService.findCommentResponsesByParentId(COMMENT_ID, pageable);

        // then
        verify(commentRepository).findAllByParentId(COMMENT_ID, pageable);
    }
}