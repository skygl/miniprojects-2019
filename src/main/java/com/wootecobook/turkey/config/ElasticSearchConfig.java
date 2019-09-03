package com.wootecobook.turkey.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    /**
     * RestHighLevelClient는 HTTP 방식을 이용해 엘라스틱서치와 통신해준다.
     * 다른 방식으로는 Transport Client가 있는데 소켓을 이용해 엘라스틱서치와 통신해서 상대적으로 빠르지만
     * 엘라스틱서치가 버전업될 때마다 제공되는 기능이나 API의 명세에 따라 클래스나 메서드가 바뀌는 문제점이 있어서 8버전 부터는 삭제될 예정이다.
     *
     * Spring-data-elasticsearch 도 있지만 최신버전(3.2)부터 RestHighLevelClient를 지원해주는데
     * SpringBoot에서는 아직 Spring-data-elasticsearch 3.2를 지원해주지 않아서 spring-data-elasticsearch를 사용하지 않았음.
     * @return
     */
    @Bean
    public RestHighLevelClient client() {
        final RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port, "http"));
        return new RestHighLevelClient(restClientBuilder);
    }
}
