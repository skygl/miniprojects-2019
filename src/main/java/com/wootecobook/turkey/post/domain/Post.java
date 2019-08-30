package com.wootecobook.turkey.post.domain;

import com.wootecobook.turkey.commons.domain.UpdatableEntity;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.post.domain.exception.InvalidPostException;
import com.wootecobook.turkey.post.domain.exception.PostUpdateFailException;
import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
public class Post extends UpdatableEntity {

    @Embedded
    private Contents contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_to_author_user"), updatable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "receiver_id", foreignKey = @ForeignKey(name = "fk_post_to_receiver_user"), updatable = false)
    private User receiver;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "post_file",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    private List<UploadFile> uploadFiles = new ArrayList<>();

    @Builder
    private Post(final Long id, final User author, final User receiver,
                 final Contents contents, final List<UploadFile> uploadFiles) {
        if (id == null) {
            validateAuthor(author);
        }
        validateContents(contents);

        this.author = author;
        this.receiver = receiver;
        this.contents = contents;
        this.uploadFiles = uploadFiles;
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

    public Post update(final Post other) {
        if (other == null) {
            throw new PostUpdateFailException();
        }

        this.contents = other.contents;

        return this;
    }

    public boolean isWrittenBy(final Long userId) {
        return author.matchId(userId);
    }

    public Optional<User> getReceiver() {
        return Optional.ofNullable(receiver);
    }
}
