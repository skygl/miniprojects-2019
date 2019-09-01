package com.wootecobook.turkey.config;

import com.wootecobook.turkey.commons.elasticsearch.utils.InitElasticSearchIndex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final InitElasticSearchIndex initElasticSearchIndex;

    public CommandLineRunnerImpl(final InitElasticSearchIndex initElasticSearchIndex) {
        this.initElasticSearchIndex = initElasticSearchIndex;
    }

    @Override
    public void run(String... args) throws Exception {
        initElasticSearchIndex.init();
    }
}