package com.wootecobook.turkey.user.service.dto;

import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String name;

    @Builder
    public UserResponse(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .id(user.getId())
                .build();
    }

}
