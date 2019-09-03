package com.wootecobook.turkey.config;

import com.wootecobook.turkey.commons.elasticsearch.utils.InitElasticSearchIndex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 서버가 실행될 때 실행시켜준다.
 */
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final InitElasticSearchIndex initElasticSearchIndex;

    public CommandLineRunnerImpl(final InitElasticSearchIndex initElasticSearchIndex) {
        this.initElasticSearchIndex = initElasticSearchIndex;
    }

    /**
     * jpa.hibernate.ddl-auto: create-drop 같은 역할
     * 엘라스틱서치의 인덱스(database)를 삭제했다가 다시 생성해준 후
     * DB(mysql, h2)에서 데이터를 가져와서 싱크를 맞춰주는 작업을 해준다.
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        initElasticSearchIndex.init();
    }
}