package com.wootecobook.turkey.comment.service.dto;

import com.wootecobook.turkey.comment.domain.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentUpdate {

    private String contents;

    public CommentUpdate(final String contents) {
        this.contents = contents;
    }

    public Comment toEntity() {
        return Comment.builder()
                .contents(contents)
                .build();
    }
}
