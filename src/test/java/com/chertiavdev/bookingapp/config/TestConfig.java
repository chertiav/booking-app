package com.chertiavdev.bookingapp.config;

import com.chertiavdev.bookingapp.data.builders.AccommodationTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.AmenityCategoryTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.AmenityTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.BookingTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.PaymentTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.StripleTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTelegramLinkTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTelegramTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public AccommodationTestDataBuilder accommodationTestBuilder(
            AmenityTestDataBuilder amenityTestDataBuilder
    ) {
        return new AccommodationTestDataBuilder(amenityTestDataBuilder);
    }

    @Bean
    public AmenityCategoryTestDataBuilder amenityCategoryTestBuilder() {
        return new AmenityCategoryTestDataBuilder();
    }

    @Bean
    public AmenityTestDataBuilder amenityTestBuilder(
            AmenityCategoryTestDataBuilder amenityCategoryTestDataBuilder
    ) {
        return new AmenityTestDataBuilder(amenityCategoryTestDataBuilder);
    }

    @Bean
    public BookingTestDataBuilder bookingTestDataBuilder(
            AccommodationTestDataBuilder accommodationTestDataBuilder,
            UserTestDataBuilder userTestDataBuilder
    ) {
        return new BookingTestDataBuilder(accommodationTestDataBuilder, userTestDataBuilder);
    }

    @Bean
    public UserTestDataBuilder userTestBuilder() {
        return new UserTestDataBuilder();
    }

    @Bean
    public UserTelegramLinkTestDataBuilder userTelegramLinkTestBuilder(
            UserTestDataBuilder userTestDataBuilder
    ) {
        return new UserTelegramLinkTestDataBuilder(userTestDataBuilder);
    }

    @Bean
    public UserTelegramTestDataBuilder userTelegramTestDataBuilder(
            UserTestDataBuilder userTestDataBuilder
    ) {
        return new UserTelegramTestDataBuilder(userTestDataBuilder);
    }

    @Bean
    public PaymentTestDataBuilder paymentTestDataBuilder(
            BookingTestDataBuilder bookingTestDataBuilder,
            StripleTestDataBuilder stripleTestDataBuilder
    ) {
        return new PaymentTestDataBuilder(bookingTestDataBuilder, stripleTestDataBuilder);
    }

    @Bean
    public StripleTestDataBuilder stripleTestDataBuilder() {
        return new StripleTestDataBuilder();
    }

}
