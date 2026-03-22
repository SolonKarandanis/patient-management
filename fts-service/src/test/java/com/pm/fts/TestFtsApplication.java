package com.pm.fts;

import org.springframework.boot.SpringApplication;

public class TestFtsApplication {

    public static void main(String[] args) {
        SpringApplication.from(FtsApplication::main).run(args);
    }

}
