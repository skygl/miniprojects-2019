package com.wootecobook.turkey.search.service;

import com.wootecobook.turkey.commons.elasticsearch.utils.AbstractKoreanParser;
import com.wootecobook.turkey.search.repository.UserDocument;
import com.wootecobook.turkey.search.repository.UserElasticSearchRepository;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.exception.SignUpException;
import org.elasticsearch.action.DocWriteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserElasticSearchServiceTest {

    @InjectMocks
    private UserElasticSearchService elasticSearchService;
    @Mock
    private UserElasticSearchRepository elasticSearchRepository;
    @Mock
    private AbstractKoreanParser koreanParser;

    private User user = new User("email@egmil.com", "배대준", "P@ssw0rd");

    @Test
    void 저장_성공() {
        // given
        final DocWriteResponse.Result result = DocWriteResponse.Result.CREATED;
        when(elasticSearchRepository.save(any(), any())).thenReturn(result);

        // when & then
        assertDoesNotThrow(() -> elasticSearchService.save(user));
        verify(elasticSearchRepository).save(any(), any());
    }

    @Test
    void 저장_실패() {
        // given
        final DocWriteResponse.Result result = DocWriteResponse.Result.NOOP;
        when(elasticSearchRepository.save(any(), any())).thenReturn(result);

        // when & then
        assertThrows(SignUpException.class, () -> elasticSearchService.save(user));
        verify(elasticSearchRepository).save(any(), any());
    }

    @Test
    void 검색() {
        // given
        final String name = "name";
        when(koreanParser.parse(name)).thenReturn(name);

        // when
        elasticSearchService.findByUserName(name);

        // then
        verify(elasticSearchRepository).findByUserName(name);
        verify(koreanParser).parse(name);
    }
}

