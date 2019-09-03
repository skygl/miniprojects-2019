package com.wootecobook.turkey.post.service.dto;

import com.wootecobook.turkey.file.service.FileResponse;
import com.wootecobook.turkey.good.service.dto.GoodResponse;
import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PostResponse {

    private static final int INIT_COMMENT_COUNT = 0;
    private Long id;
    private UserResponse author;
    private UserResponse receiver;
    private List<UserResponse> taggedUsers;
    private Contents contents;
    private List<FileResponse> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalComment;
    private GoodResponse goodResponse;

    @Builder
    private PostResponse(final Long id, final UserResponse author, final UserResponse receiver, final Contents contents,
                         final List<FileResponse> fileResponses, final LocalDateTime createdAt, final LocalDateTime updatedAt,
                         final Integer totalComment, final GoodResponse goodResponse, List<UserResponse> taggedUsers) {
        this.id = id;
        this.author = author;
        this.receiver = receiver;
        this.contents = contents;
        this.files = fileResponses;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalComment = totalComment;
        this.goodResponse = goodResponse;
        this.taggedUsers = taggedUsers;
    }

    public static PostResponse getPostResponse(final Post post) {
        return from(post, GoodResponse.init(), INIT_COMMENT_COUNT);
    }

    public static PostResponse from(final Post post, final GoodResponse goodResponse, final int totalComment) {
        List<FileResponse> fileResponses = convertFileResponsesFromUploadFiles(post);
        UserResponse receiverResponse = convertReceiverResponseFromReceiver(post);
        List<UserResponse> taggedUserResponses = convertTaggedUserResponsesFromTaggedUsers(post);

        return PostResponse.builder()
                .id(post.getId())
                .author(UserResponse.from(post.getAuthor()))
                .receiver(receiverResponse)
                .contents(post.getContents())
                .fileResponses(fileResponses)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .goodResponse(goodResponse)
                .totalComment(totalComment)
                .taggedUsers(taggedUserResponses)
                .build();
    }

    private static List<UserResponse> convertTaggedUserResponsesFromTaggedUsers(final Post post) {
        return post.getTaggedUsers().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    private static UserResponse convertReceiverResponseFromReceiver(final Post post) {
        return post.getReceiver()
                .map(UserResponse::from)
                .orElse(null);
    }

    private static List<FileResponse> convertFileResponsesFromUploadFiles(final Post post) {
        return post.getUploadFiles().stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }
}
