package com.chertiavdev.bookingapp.config;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TEST_TOKEN_CURRENT;

import com.chertiavdev.bookingapp.util.helpers.token.generator.UuidTokenGenerator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UuidTokenGeneratorTestConfig {
    @Bean
    public UuidTokenGenerator uuidTokenGenerator() {
        return new UuidTokenGenerator() {
            @Override
            public String generateToken() {
                return TEST_TOKEN_CURRENT;
            }
        };
    }
}
