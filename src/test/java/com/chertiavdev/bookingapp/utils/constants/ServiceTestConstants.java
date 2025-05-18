package com.chertiavdev.bookingapp.utils.constants;

import java.math.BigDecimal;
import java.util.List;

public final class ServiceTestConstants {
    public static final String ACCOMMODATION_SIZE = "Studio";
    public static final BigDecimal ACCOMMODATION_DAILY_RATE = BigDecimal.valueOf(75.50);
    public static final int ACCOMMODATION_AVAILABILITY = 1;
    public static final List<Long> ACCOMMODATION_DEFAULT_AMENITIES = List.of(1L, 2L, 3L);
    public static final String ADDRESS_STREET_KHRESHCHATYK = "Khreshchatyk";
    public static final String ADDRESS_CITY_KYIV = "Kyiv";
    public static final String ADDRESS_HOUSE_NUMBER_15B = "15B";
    public static final String ADDRESS_APARTMENT_NUMBER_25 = "25";
    public static final int TEST_AVAILABILITY_THRESHOLD = 0;
    public static final long SAMPLE_TEST_ID_1 = 1L;
    public static final long SAMPLE_TEST_ID_2 = 2L;
    public static final String ACCOMMODATION_NOT_FOUND_MESSAGE = "Can't find accommodation by id: ";
    public static final String ACCOMMODATION_UPDATE_ERROR_MESSAGE =
            "Can't update accommodation by id: ";
    public static final String AMENITY_CATEGORY_NOT_FOUND_MESSAGE =
            "Can't find amenity category by id: ";
    public static final String AMENITY_CATEGORY_NAME_UPDATE = "Updated category name";
    public static final String AMENITY_CATEGORY_UPDATE_ERROR_MESSAGE =
            "Can't update category by id: ";
}
