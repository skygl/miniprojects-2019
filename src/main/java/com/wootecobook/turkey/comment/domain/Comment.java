package com.wootecobook.turkey.comment.domain;

import com.wootecobook.turkey.comment.domain.exception.CommentUpdateFailException;
import com.wootecobook.turkey.comment.domain.exception.NotCommentOwnerException;
import com.wootecobook.turkey.comment.service.exception.AlreadyDeleteException;
import com.wootecobook.turkey.commons.domain.UpdatableEntity;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@Where(clause = "deleted = 0")
public class Comment extends UpdatableEntity {

    protected static final String CONTENTS_DELETE_MESSAGE = "삭제된 글입니다.";

    @Lob
    @Column(nullable = false)
    private String contents;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_comment_to_user"), nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_comment_to_post"), nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_comment_to_comment"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(final String contents, final User author, final Post post, final Comment parent) {
        CommentValidator.validateContents(contents);

        this.contents = contents;
        this.author = author;
        this.post = post;
        this.parent = parent;
        this.deleted = false;
    }

    public boolean isWrittenBy(Long userId) {
        if (this.author.getId().equals(userId)) {
            return true;
        }
        throw new NotCommentOwnerException();
    }

    public void update(final Comment other) {
        if (other == null) {
            throw new CommentUpdateFailException();
        }
        validateDelete();
        this.contents = other.contents;
    }

    public void delete() {
        validateDelete();
        deleted = true;
        contents = CONTENTS_DELETE_MESSAGE;
    }

    private void validateDelete() {
        if (this.deleted) {
            throw new AlreadyDeleteException(this.getId());
        }
    }

    public int getCountOfChildren() {
        return children.size();
    }

    public Optional<Comment> getParent() {
        return Optional.ofNullable(parent);
    }

    public Optional<Long> getParentCommentId() {
        if (parent == null) {
            return Optional.empty();
        }
        return Optional.of(parent.getId());
    }
}

