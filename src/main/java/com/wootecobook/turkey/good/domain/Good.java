package com.wootecobook.turkey.good.domain;

import com.wootecobook.turkey.commons.domain.BaseEntity;
import com.wootecobook.turkey.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.INTEGER, columnDefinition = "TINYINT(1)")
public abstract class Good extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_USER_TO_GOOD"))
    private User user;

    protected Good(final User user) {
        this.user = user;
    }
}
