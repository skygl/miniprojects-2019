package com.wootecobook.turkey.post.domain;

import com.wootecobook.turkey.post.domain.exception.InvalidContentException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Contents {

    @Lob
    @Column(nullable = false)
    private String contents;

    public Contents(final String contents) {
        validate(contents);
        this.contents = contents;
    }

    private void validate(String contents) {
        if (contents == null || contents.trim().equals("")) {
            throw new InvalidContentException();
        }
    }
}