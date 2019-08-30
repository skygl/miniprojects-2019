package com.wootecobook.turkey.post.service;

import com.wootecobook.turkey.comment.domain.CommentRepository;
import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.file.service.UploadFileService;
import com.wootecobook.turkey.good.service.PostGoodService;
import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.post.domain.PostRepository;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import com.wootecobook.turkey.post.service.exception.NotPostOwnerException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private static final long NOT_FOUND_POST_ID = Long.MAX_VALUE;

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostGoodService postGoodService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private UploadFileService uploadFileService;

    private Contents defaultContents;
    private Post savedPost;
    private PostRequest postRequestWithoutFiles;
    private PostRequest updatePostRequest;
    private User author;
    private User other;
    private Long savedPostId;

    @BeforeEach
    void setUp() {
        author = new User("pobi@woowa.com", "pobi", "Passw0rd!");
        author.setId(1L);
        other = new User("olaf@woowa.com", "olaf", "Passw0rd!");
        other.setId(2L);
        String contentsText = "hello";
        postRequestWithoutFiles = PostRequest.builder()
                .contents(contentsText)
                .build();
        defaultContents = new Contents(contentsText);
        updatePostRequest = PostRequest.builder()
                .contents("update")
                .build();
        savedPostId = 10L;
        savedPost = Post.builder()
                .contents(defaultContents)
                .author(author)
                .uploadFiles(new ArrayList<>())
                .id(savedPostId)
                .build();
    }

    @Test
    void 파일첨부_post_생성_테스트() {
        //given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "mock1.png", "image/png", "test data".getBytes());
        FileFeature fileFeature = convertToFileFeature(mockMultipartFile);
        UploadFile uploadFile = new UploadFile(fileFeature, author);
        when(userService.findById(any(Long.class))).thenReturn(author);
        when(postRepository.save(any(Post.class))).then(returnsFirstArg());
        when(uploadFileService.save(any(MultipartFile.class), any(), any(User.class))).thenReturn(uploadFile);
        //when
        PostRequest postRequestWithFiles = PostRequest.builder()
                .contents(defaultContents.getContents())
                .files(Arrays.asList(mockMultipartFile))
                .build();
        PostResponse result = postService.save(postRequestWithFiles, author.getId());
        //then
        assertThat(result.getContents()).isEqualTo(defaultContents);
        assertThat(result.getFiles()).hasSize(1);
    }

    @Test
    void 파일없이_post_생성_테스트() {
        //given
        when(userService.findById(any(Long.class))).thenReturn(author);
        when(postRepository.save(any(Post.class))).then(returnsFirstArg());
        //when
        PostResponse result = postService.save(postRequestWithoutFiles, author.getId());
        //then
        assertThat(result.getContents()).isEqualTo(defaultContents);
        assertThat(result.getFiles()).hasSize(0);
    }

    @Test
    void post_조회_테스트() {
        //given
        when(postRepository.findById(savedPostId)).thenReturn(Optional.of(savedPost));
        //when
        Post testPost = postService.findById(savedPostId);
        //then
        assertThat(testPost.getContents()).isEqualTo(defaultContents);
    }

    @Test
    void 존재하지_않는_post_조회_예외_테스트() {
        //when & then
        assertThrows(EntityNotFoundException.class, () -> postService.findById(NOT_FOUND_POST_ID));
    }

    //  todo : 리팩터링 필요
//    @Test
//    void post_페이징_테스트() {
//        int pageNum = 0;
//        IntStream.rangeClosed(1, 100).forEach(i ->
//                postService.save(PostRequest.builder().contents("hello" + i).build(), author.getId()));
//
//        Page<PostResponse> pageResponse = postService.findPostResponses(PageRequest.from(pageNum, 10));
//
//        assertThat(pageResponse.getTotalElements()).isEqualTo(100);
//        assertThat(pageResponse.getTotalPages()).isEqualTo(10);
//        assertThat(pageResponse.getNumber()).isEqualTo(pageNum);
//
//        List<PostResponse> postResponses = pageResponse.getContent();
//
//        for (int i = 1; i <= 10; i++) {
//            PostResponse postResponse = postResponses.get(i - 1);
//            assertThat(postResponse.getContents()).isEqualTo(new Contents("hello" + i));
//        }
//    }

    @Test
    void post_수정_테스트() {
        //given
        when(postRepository.findById(savedPostId)).thenReturn(Optional.of(savedPost));
        when(postGoodService.countBy(any(Post.class))).thenReturn(0);
        when(commentRepository.countByPost(any(Post.class))).thenReturn(0);
        //when
        PostResponse updateResult = postService.update(updatePostRequest, savedPostId, author.getId());
        //then
        assertThat(updateResult.getContents().getContents()).isEqualTo(updatePostRequest.getContents());
    }

    @Test
    void 없는_게시글_수정_예외_테스트() {
        //when & then
        assertThrows(EntityNotFoundException.class, () ->
                postService.update(postRequestWithoutFiles, NOT_FOUND_POST_ID, author.getId()));
    }

    @Test
    void 수정_권한이_없는_유저_게시글_수정_예외_테스트() {
        //given
        when(postRepository.findById(savedPostId)).thenReturn(Optional.of(savedPost));
        //when & then
        assertThrows(NotPostOwnerException.class, () -> postService.update(updatePostRequest, savedPostId, other.getId()));
    }

    @Test
    void post_삭제_테스트() {
        //given
        when(postRepository.findById(savedPostId)).thenReturn(Optional.of(savedPost));
        //when & then
        assertDoesNotThrow(() -> postService.delete(savedPostId, author.getId()));
    }

    @Test
    void 없는_post_삭제_예외_테스트() {
        //when & then
        assertThrows(EntityNotFoundException.class, () -> postService.delete(NOT_FOUND_POST_ID, author.getId()));
    }

    @Test
    void 삭제_권한이_없는_유저_게시글_삭제_예외_테스트() {
        //given
        when(postRepository.findById(savedPostId)).thenReturn(Optional.of(savedPost));
        //when & then
        assertThrows(NotPostOwnerException.class, () -> postService.delete(savedPostId, other.getId()));
    }

    private FileFeature convertToFileFeature(MultipartFile multipartFile) {
        return FileFeature.builder()
                .path("testfilePath")
                .originalName(multipartFile.getOriginalFilename())
                .type(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .build();
    }
}