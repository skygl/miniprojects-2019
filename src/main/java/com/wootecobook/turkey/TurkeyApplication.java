package com.wootecobook.turkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class TurkeyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurkeyApplication.class, args);
    }

}
