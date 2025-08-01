package com.chertiavdev.bookingapp.util.helpers.token.generator;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
