package com.exam.exam_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExamTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamTestApplication.class, args);
    }

}
