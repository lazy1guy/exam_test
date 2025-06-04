package com.exam.exam_system.config;


import lombok.Setter;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileUploadProperties {

    private String uploadDir;

}
