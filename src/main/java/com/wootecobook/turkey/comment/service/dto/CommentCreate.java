package com.wootecobook.turkey.comment.service.dto;

import com.wootecobook.turkey.comment.domain.Comment;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentCreate {

    private String contents;
    private Long parentId;

    public CommentCreate(final String contents, final Long parentId) {
        this.contents = contents;
        this.parentId = parentId;
    }

    public Comment toEntity(final User user, final Post post, final Comment parent) {
        return Comment.builder()
                .contents(contents)
                .user(user)
                .post(post)
                .parent(parent)
                .build();
    }
}
