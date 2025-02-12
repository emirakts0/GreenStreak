package com.emir.gitautocommit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class GitAutoCommitApplication {
    public static void main(String[] args) {
        SpringApplication.run(GitAutoCommitApplication.class, args);
    }
}
