package com.wootecobook.turkey.search.repository;

import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDocument {

    private String name;
    private String nameJamo;
    private String nameChosung;

    @Builder
    public UserDocument(final String name, final String nameJamo, final String nameChosung) {
        this.name = name;
        this.nameJamo = nameJamo;
        this.nameChosung = nameChosung;
    }

    public static UserDocument from(User user) {
        return UserDocument.builder()
                .name(user.getName())
                .nameJamo(user.getName())
                .nameChosung(user.getName())
                .build();
    }
}
