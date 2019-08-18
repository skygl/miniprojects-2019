package com.wootecobook.turkey.post.domain;

import com.wootecobook.turkey.post.domain.exception.InvalidContentException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContentsTest {

    @Test
    void 생성() {
        String contentsText = "hello";
        Contents contents = new Contents(contentsText);

        assertThat(contents).isEqualTo(new Contents(contentsText));
    }

    @Test
    void 내용이_빈_공백인_경우_예외_테스트() {
        assertThrows(InvalidContentException.class, () -> new Contents(""));
    }

    @Test
    void 내용이_null인_경우_예외_테스트() {
        assertThrows(InvalidContentException.class, () -> new Contents(null));
    }

}
