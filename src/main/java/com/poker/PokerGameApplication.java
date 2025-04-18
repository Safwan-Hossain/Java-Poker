package com.poker;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PokerGameApplication{

    public static void main(String[] args) {
        SpringApplication.run(PokerGameApplication.class, args);
    }

}

