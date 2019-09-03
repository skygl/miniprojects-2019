package com.wootecobook.turkey.commons.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class ElasticSearchTemplate {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchTemplate.class);

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    public ElasticSearchTemplate(final RestHighLevelClient client, final ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    /**
     * Document(=row) 추가 API
     * @param source
     * @param index
     * @param id
     * @return
     */
    public DocWriteResponse.Result add(final Object source, final String index, final String id) {
        try {
            final String jsonSource = objectMapper.writeValueAsString(source);
            final IndexRequest request = new IndexRequest(index).id(id).source(jsonSource, XContentType.JSON);
            return client.index(request, RequestOptions.DEFAULT).getResult();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return DocWriteResponse.Result.NOOP;
    }

    /**
     * 검색 API
     * @param index
     * @param searchSourceBuilder
     * @return
     */
    public SearchHits search(final String index, final SearchSourceBuilder searchSourceBuilder) {
        SearchHits searchHits = SearchHits.empty();
        try {
            final SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source(searchSourceBuilder);

            final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            searchHits = searchResponse.getHits();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return searchHits;
    }

    /**
     * 인덱스(Database) 삭제 API
     * @param index
     * @return
     */
    public boolean deleteIndex(final String index) {
        boolean result = false;
        try {
            final DeleteIndexRequest request = new DeleteIndexRequest(index);
            final AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
            result = deleteIndexResponse.isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 인덱스(Database) 생성 API
     * @param request
     * @return
     */
    public boolean createIndex(final CreateIndexRequest request) {
        boolean result = false;
        try {
            final CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            result = createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 한 번의 API 호출로 여러 문서를 업데이트 (추가, 수정, 삭제)
     * http 요청수를 줄일수 있으므로 매우 중요하다.
     * @param indexRequests
     */
    public void bulk(final List<IndexRequest> indexRequests) {
        try {
            final BulkRequest request = new BulkRequest();
            indexRequests.forEach(request::add);
            final BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
