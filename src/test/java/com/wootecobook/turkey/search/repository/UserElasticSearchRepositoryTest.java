package com.wootecobook.turkey.search.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecobook.turkey.commons.elasticsearch.ElasticSearchTemplate;
import com.wootecobook.turkey.search.service.dto.UserSearchResponse;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserElasticSearchRepositoryTest {

    @Autowired
    private UserElasticSearchRepository userElasticSearchRepository;

    private final String name = "배대준";
    private final UserDocument userDocument = new UserDocument(name, name, name);

    //TODO 롤백 어떻게? 그냥 삭제해야 한다면, 아이디 중복 처리? (@Transactional 안됨)
    @Test
    void 저장() {
        // given
        final DocWriteResponse.Result actual = DocWriteResponse.Result.CREATED;

        // when
        final DocWriteResponse.Result expected = userElasticSearchRepository.save(userDocument, 1700L);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 이름으로_조회() {
        // given
        final String name = "ㅂㄷㅈ";

        // when
        final List<UserSearchResponse> responses = userElasticSearchRepository.findByUserName(name);

        // then
        assertThat(responses).isNotEmpty();
    }
}