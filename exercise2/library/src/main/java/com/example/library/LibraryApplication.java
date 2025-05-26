package com.example.library;

import com.example.library.console.LibraryConsole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Profile("!test") // The console menu will not run when test profile is active
    public CommandLineRunner commandLineRunner(LibraryConsole libraryConsole) {
        return args -> {
            libraryConsole.start();
        };
    }
}
