package com.chertiavdev.bookingapp.repository.accommodation;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_AVAILABILITY;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_DEFAULT_AMENITIES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_SIZE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_APARTMENT_NUMBER_25;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_CITY_KYIV;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_HOUSE_NUMBER_15B;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ADDRESS_STREET_KHRESHCHATYK;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AVAILABILITY_THRESHOLD;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.accommodationFromRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSampleAccommodationRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.getAmenitiesById;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chertiavdev.bookingapp.model.Accommodation;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("Accommodation Repository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccommodationRepositoryTest {
    @Autowired
    private AccommodationRepository accommodationRepository;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        setupDatabase(dataSource);
    }

    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/accommodation/address/add-address-into-address-table.sql",
                    "database/accommodation/add-accommodations-into-accommodations-table.sql",
                    "database/amenities/"
                            + "add-amenities-into-accommodation_amenities-table.sql"
            );
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/amenities/"
                           + "remove-all-amenities-from-accommodation_amenities-table.sql",
                    "database/accommodation/remove-all-accommodations-from-accommodation-table.sql",
                    "database/accommodation/address/remove-all-address-from-address-table.sql"
            );
        }
    }

    @Test
    @DisplayName("Find accommodation successfully when valid data is provided should return true")
    void existsByLocationAndTypeAndSize_ValidData_ShouldReturnTrue() {
        //When
        boolean actual = accommodationRepository.existsByLocationAndTypeAndSize(
                ADDRESS_CITY_KYIV,
                ADDRESS_STREET_KHRESHCHATYK,
                ADDRESS_HOUSE_NUMBER_15B,
                ADDRESS_APARTMENT_NUMBER_25,
                Accommodation.Type.HOUSE,
                ACCOMMODATION_SIZE
        );

        //Then
        assertTrue(actual, ACTUAL_RESULT_SHOULD_BE_PRESENT);
    }

    @Test
    @DisplayName("Find accommodation when invalid data is provided should return false")
    void existsByLocationAndTypeAndSize_InValidData_ShouldReturnFalse() {
        //When
        boolean actual = accommodationRepository.existsByLocationAndTypeAndSize(
                ADDRESS_CITY_KYIV,
                ADDRESS_STREET_KHRESHCHATYK,
                ADDRESS_HOUSE_NUMBER_15B,
                ADDRESS_APARTMENT_NUMBER_25,
                Accommodation.Type.APARTMENT,
                ACCOMMODATION_SIZE
        );

        //Then
        assertFalse(actual, ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Find accommodation successfully when valid data is provided should "
            + "return accommodation")
    void findAllByAvailabilityGreaterThan_ValidData_ShouldReturnAccommodation() {
        //Given
        Accommodation accommodation = accommodationFromRequestDto(
                createSampleAccommodationRequest());
        accommodation.getLocation().setId(SAMPLE_TEST_ID_1);
        accommodation.setId(SAMPLE_TEST_ID_1);
        accommodation.setAmenities(getAmenitiesById(ACCOMMODATION_DEFAULT_AMENITIES));

        List<Accommodation> accommodations = List.of(accommodation);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Accommodation> expected = new PageImpl<>(accommodations,
                pageable, accommodations.size());

        //When
        Page<Accommodation> actual = accommodationRepository
                .findAllByAvailabilityGreaterThan(AVAILABILITY_THRESHOLD, pageable);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(
                expected.getNumberOfElements(),
                actual.getNumberOfElements(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalPages(),
                actual.getTotalPages(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalElements(),
                actual.getTotalElements(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);

    }

    @Test
    @DisplayName("Find accommodation successfully when valid data is provided should return "
            + "Accommodation")
    void findByIdAndAvailabilityGreaterThan_ValidData_ShouldReturnAccommodation() {
        //Given
        Accommodation expected = accommodationFromRequestDto(
                createSampleAccommodationRequest());
        expected.getLocation().setId(SAMPLE_TEST_ID_1);
        expected.setId(SAMPLE_TEST_ID_1);
        expected.setAmenities(getAmenitiesById(ACCOMMODATION_DEFAULT_AMENITIES));

        //When
        Optional<Accommodation> actual = accommodationRepository
                .findByIdAndAvailabilityGreaterThan(SAMPLE_TEST_ID_1, AVAILABILITY_THRESHOLD);

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find accommodation when valid Id is provided and availability less than 1 should "
            + "return optional empty")
    void findByIdAndAvailabilityGreaterThan_ValidIdAndAvailabilityLessThanOne_ShouldReturnEmpty() {
        //When
        Optional<Accommodation> actual = accommodationRepository
                .findByIdAndAvailabilityGreaterThan(SAMPLE_TEST_ID_2, ACCOMMODATION_AVAILABILITY);

        //Then
        assertTrue(actual.isEmpty(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }
}
