package com.wootecobook.turkey;

import com.wootecobook.turkey.commons.elasticsearch.utils.InitElasticSearchIndex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

@ServletComponentScan
@SpringBootApplication
public class TurkeyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurkeyApplication.class, args);
    }

}
