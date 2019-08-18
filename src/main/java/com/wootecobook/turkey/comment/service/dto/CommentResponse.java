package com.wootecobook.turkey.comment.service.dto;


import com.wootecobook.turkey.comment.domain.Comment;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private Long parentId;
    private String contents;
    private UserResponse userResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CommentResponse(final Long id, final Long parentId, final String contents,
                           final UserResponse userResponse, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.parentId = parentId;
        this.contents = contents;
        this.userResponse = userResponse;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .contents(comment.getContents())
                .parentId(comment.getParentCommentId().orElse(null))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

}
