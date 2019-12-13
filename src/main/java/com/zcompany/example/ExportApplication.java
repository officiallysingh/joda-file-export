package com.zcompany.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@RestController
@SpringBootApplication
public class ExportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExportApplication.class, args);
    }
}
