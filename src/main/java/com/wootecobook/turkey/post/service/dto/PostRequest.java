package com.wootecobook.turkey.post.service.dto;

import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class PostRequest {

    @NotBlank
    private String contents;

    public PostRequest(final String contents) {
        this.contents = contents;
    }

    public Post toEntity(User author) {
        return Post.builder()
                .contents(new Contents(contents))
                .author(author)
                .build();
    }
}
