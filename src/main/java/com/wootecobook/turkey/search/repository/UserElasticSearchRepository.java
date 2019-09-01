package com.wootecobook.turkey.search.repository;

import com.wootecobook.turkey.commons.elasticsearch.ElasticSearchTemplate;
import com.wootecobook.turkey.search.service.dto.UserSearchResponse;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserElasticSearchRepository {

    public static final String INDEX_NAME = "user";

    private final ElasticSearchTemplate elasticSearchTemplate;

    public UserElasticSearchRepository(final ElasticSearchTemplate elasticSearchTemplate) {
        this.elasticSearchTemplate = elasticSearchTemplate;
    }

    public DocWriteResponse.Result save(final UserDocument userDocument, final Long id) {
        return elasticSearchTemplate.add(userDocument, INDEX_NAME, String.valueOf(id));
    }

    public List<UserSearchResponse> findByUserName(final String name) {
        final List<UserSearchResponse> userSearchResponses = new ArrayList<>();

        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder
                .should(QueryBuilders.prefixQuery("name", name))
                .should(QueryBuilders.termQuery("name", name))
                .should(QueryBuilders.termQuery("nameJamo", name))
                .should(QueryBuilders.termQuery("nameChosung", name))
                .minimumShouldMatch(1);

        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(boolQueryBuilder).from(0).size(8);
        final SearchHits searchHits = elasticSearchTemplate.search(INDEX_NAME, searchSourceBuilder);

        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            userSearchResponses.add(new UserSearchResponse(hit.getId(), sourceAsMap.get("name").toString()));
        }
        return userSearchResponses;
    }
}
