package com.wootecobook.turkey.search.service;

import com.wootecobook.turkey.search.repository.UserElasticSearchRepository;
import com.wootecobook.turkey.search.repository.UserDocument;
import com.wootecobook.turkey.search.service.dto.UserSearchResponse;
import com.wootecobook.turkey.commons.elasticsearch.utils.AbstractKoreanParser;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.exception.SignUpException;
import org.elasticsearch.action.DocWriteResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserElasticSearchService {

    private final UserElasticSearchRepository userElasticSearchRepository;
    private final AbstractKoreanParser koreanJamoParser;

    public UserElasticSearchService(final UserElasticSearchRepository userElasticSearchRepository, final AbstractKoreanParser koreanJamoParser) {
        this.userElasticSearchRepository = userElasticSearchRepository;
        this.koreanJamoParser = koreanJamoParser;
    }

    public boolean save(final User user) {
        final UserDocument userDocument = UserDocument.from(user);
        final DocWriteResponse.Result result = userElasticSearchRepository.save(userDocument, user.getId());

        if (!result.equals(DocWriteResponse.Result.CREATED)) {
            throw new SignUpException("elasticSearch 에 저장 실패");
        }
        return true;
    }

    public List<UserSearchResponse> findByUserName(final String name) {
        final String parsedName = koreanJamoParser.parse(name);
        return userElasticSearchRepository.findByUserName(parsedName);
    }
}
