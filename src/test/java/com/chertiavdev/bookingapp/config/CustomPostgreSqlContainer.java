package com.chertiavdev.bookingapp.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgreSqlContainer extends PostgreSQLContainer<CustomPostgreSqlContainer> {
    private static final String DB_IMAGE = "postgres:17-alpine";
    private static CustomPostgreSqlContainer postgreSqlContainer;

    public CustomPostgreSqlContainer() {
        super(DB_IMAGE);
    }

    public static CustomPostgreSqlContainer getInstance() {
        if (postgreSqlContainer == null) {
            postgreSqlContainer = new CustomPostgreSqlContainer();
        }
        return postgreSqlContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", postgreSqlContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", postgreSqlContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", postgreSqlContainer.getPassword());
    }

    @Override
    public void stop() {
    }
}
