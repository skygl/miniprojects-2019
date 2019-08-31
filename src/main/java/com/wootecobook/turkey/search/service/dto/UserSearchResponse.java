package com.wootecobook.turkey.search.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class UserSearchResponse {

    private String id;
    private String name;

    public UserSearchResponse(final String id, final String name) {
        this.id = id;
        this.name = name;
    }
}
