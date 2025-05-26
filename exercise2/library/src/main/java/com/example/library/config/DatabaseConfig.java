package com.example.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Extract the database file path from the URL
        String dbPath = dbUrl.replace("jdbc:sqlite:", "");
        Path dbFilePath = Paths.get(dbPath);

        // Create the data directory if it doesn't exist
        try {
            Files.createDirectories(dbFilePath.getParent());
            log.info("Database directory created/verified at: {}", dbFilePath.getParent());
        } catch (Exception e) {
            log.error("Error creating database directory: {}", e.getMessage());
            throw new RuntimeException("Failed to create database directory", e);
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dbUrl);
        log.info("Configured database source with URL: {}", dbUrl);
        return dataSource;
    }
}
