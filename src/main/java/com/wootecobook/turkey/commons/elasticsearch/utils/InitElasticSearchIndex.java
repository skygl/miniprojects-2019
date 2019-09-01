package com.wootecobook.turkey.commons.elasticsearch.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecobook.turkey.commons.elasticsearch.ElasticSearchTemplate;
import com.wootecobook.turkey.search.repository.UserDocument;
import com.wootecobook.turkey.search.repository.UserElasticSearchRepository;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InitElasticSearchIndex {
    private static final Logger log = LoggerFactory.getLogger(InitElasticSearchIndex.class);

    private final ElasticSearchTemplate elasticSearchTemplate;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public InitElasticSearchIndex(final ElasticSearchTemplate elasticSearchTemplate, final UserRepository userRepository, final ObjectMapper objectMapper) {
        this.elasticSearchTemplate = elasticSearchTemplate;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public void init() {
        final String index = UserElasticSearchRepository.INDEX_NAME;

        deleteIndex(index);

        createIndex(index);

        bulkUserDocument(index);
    }

    private void deleteIndex(final String index) {
        try {
            final boolean deleteResult = elasticSearchTemplate.deleteIndex(index);
            log.info("deleteIndex : {}", deleteResult);
        } catch (ElasticsearchStatusException e) {
            log.info(e.getMessage());
        }
    }

    private void createIndex(final String index) {
        final CreateIndexRequest request = new CreateIndexRequest(index);
        request.source("{\n" +
                "  \"settings\" : {\n" +
                "    \"index\" : {\n" +
                "      \"number_of_shards\" : 5,\n" +
                "      \"number_of_replicas\" : 1\n" +
                "    },\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"chosung_index_analyzer\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"keyword\",\n" +
                "          \"filter\": [\n" +
                "            \"javacafe_chosung_filter\",\n" +
                "            \"lowercase\",\n" +
                "            \"trim\",\n" +
                "            \"edge_ngram_filter_front\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"chosung_search_analyzer\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"keyword\",\n" +
                "          \"filter\": [\n" +
                "            \"javacafe_chosung_filter\",\n" +
                "            \"lowercase\",\n" +
                "            \"trim\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"jamo_index_analyzer\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"keyword\",\n" +
                "          \"filter\": [\n" +
                "            \"javacafe_jamo_filter\",\n" +
                "            \"lowercase\",\n" +
                "            \"trim\",\n" +
                "            \"edge_ngram_filter_front\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"jamo_search_analyzer\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"keyword\",\n" +
                "          \"filter\": [\n" +
                "            \"javacafe_jamo_filter\",\n" +
                "            \"lowercase\",\n" +
                "            \"trim\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"tokenizer\" : {\n" +
                "        \"edge_ngram_tokenizer\" : {\n" +
                "          \"type\" : \"edgeNGram\",\n" +
                "          \"min_gram\" : \"1\",\n" +
                "          \"max_gram\" : \"50\",\n" +
                "          \"token_chars\" : [\n" +
                "            \"letter\",\n" +
                "            \"digit\",\n" +
                "            \"punctuation\",\n" +
                "            \"symbol\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"filter\": {\n" +
                "        \"edge_ngram_filter_front\" : {\n" +
                "          \"type\" : \"edgeNGram\",\n" +
                "          \"min_gram\" : \"1\",\n" +
                "          \"max_gram\" : \"50\",\n" +
                "          \"side\" : \"front\"\n" +
                "        },\n" +
                "        \"javacafe_chosung_filter\": {\n" +
                "          \"type\": \"javacafe_chosung\"\n" +
                "        },\n" +
                "        \"javacafe_jamo_filter\": {\n" +
                "          \"type\": \"javacafe_jamo\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n", XContentType.JSON);

        request.mapping("{\n" +
                "  \"properties\": {\n" +
                "    \"name\": {\n" +
                "      \"type\": \"keyword\",\n" +
                "      \"boost\": 30\n" +
                "    },\n" +
                "    \"nameJamo\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"jamo_index_analyzer\",\n" +
                "      \"search_analyzer\": \"jamo_search_analyzer\",\n" +
                "      \"boost\": 10\n" +
                "    },\n" +
                "    \"nameChosung\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"chosung_index_analyzer\",\n" +
                "      \"search_analyzer\": \"chosung_search_analyzer\",\n" +
                "      \"boost\": 10\n" +
                "    }\n" +
                "  }\n" +
                "}\n", XContentType.JSON);

        final boolean createResult = elasticSearchTemplate.createIndex(request);
        log.info("createIndex : {}", createResult);
    }

    private void bulkUserDocument(final String index) {
        final List<User> users = userRepository.findAll();
        final List<IndexRequest> indexRequests = users.stream()
                .map(user -> {
                    UserDocument userDocument = UserDocument.from(user);
                    String jsonSource = "";
                    try {
                        jsonSource = objectMapper.writeValueAsString(userDocument);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return new IndexRequest(index).id(String.valueOf(user.getId())).source(jsonSource, XContentType.JSON);
                })
                .collect(Collectors.toList());

        elasticSearchTemplate.bulk(indexRequests);
    }
}
