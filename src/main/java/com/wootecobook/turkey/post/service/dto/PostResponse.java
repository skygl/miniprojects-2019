package com.wootecobook.turkey.post.service.dto;

import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostResponse {

    private Long id;
    private UserResponse author;
    private Contents contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private PostResponse(Long id, UserResponse author, Contents contents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.author = author;
        this.contents = contents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostResponse from(final Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .author(UserResponse.from(post.getAuthor()))
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

}
