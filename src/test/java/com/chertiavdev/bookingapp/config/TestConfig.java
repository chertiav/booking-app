package com.chertiavdev.bookingapp.config;

import com.chertiavdev.bookingapp.data.builders.BookingTestDataBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public BookingTestDataBuilder bookingTestDataBuilder() {
        return new BookingTestDataBuilder();
    }
}

