package com.wootecobook.turkey;

import com.wootecobook.turkey.commons.elasticsearch.utils.InitElasticSearchIndex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class TurkeyApplication implements CommandLineRunner {

    private final InitElasticSearchIndex initElasticSearchIndex;

    public TurkeyApplication(final InitElasticSearchIndex initElasticSearchIndex) {
        this.initElasticSearchIndex = initElasticSearchIndex;
    }

    public static void main(String[] args) {
        SpringApplication.run(TurkeyApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        initElasticSearchIndex.init();
    }
}
