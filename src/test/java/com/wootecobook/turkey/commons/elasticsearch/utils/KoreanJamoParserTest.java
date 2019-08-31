package com.wootecobook.turkey.commons.elasticsearch.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class KoreanJamoParserTest {
    private final AbstractKoreanParser koreanParser = new KoreanJamoParser();

    @ParameterizedTest
    @MethodSource("params")
    void 초성_분리(String token, String actual) {
        // given

        // when
        final String expected = koreanParser.parse(token);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> params() {
        return Stream.of(
                Arguments.of("베디", "ㅂㅔㄷㅣ"),
                Arguments.of("올라프", "ㅇㅗㄹㄹㅏㅍㅡ"),
                Arguments.of("dpudpu", "dpudpu")
        );
    }
}