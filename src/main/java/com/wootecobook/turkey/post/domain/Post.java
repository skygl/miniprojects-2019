package com.wootecobook.turkey.post.domain;

import com.wootecobook.turkey.commons.BaseEntity;
import com.wootecobook.turkey.post.domain.exception.InvalidPostException;
import com.wootecobook.turkey.post.domain.exception.PostUpdateFailException;
import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {

    @Embedded
    private Contents contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_to_user"))
    private User author;

    @Builder
    private Post(final Long id, final Contents contents, final User author) {
        if (id == null) {
            validateAuthor(author);
        }
        validateContents(contents);

        this.contents = contents;
        this.author = author;
    }

    private void validateContents(final Contents contents) {
        if (contents == null) {
            throw new InvalidPostException();
        }
    }

    private void validateAuthor(final User author) {
        if (author == null) {
            throw new InvalidPostException();
        }
    }

    public Post update(Post other) {
        if (other == null) {
            throw new PostUpdateFailException();
        }

        this.contents = other.contents;

        return this;
    }

    public boolean isWrittenBy(final Long userId) {
        return author.matchId(userId);
    }
}
