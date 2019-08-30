package com.wootecobook.turkey.good.domain.post;

import com.wootecobook.turkey.good.domain.Good;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@DiscriminatorValue("1")
public class PostGood extends Good {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "POST_ID", foreignKey = @ForeignKey(name = "FK_POST_TO_GOOD"))
    private Post post;

    public PostGood(final User user, final Post post) {
        super(user);
        this.post = post;
    }
}
