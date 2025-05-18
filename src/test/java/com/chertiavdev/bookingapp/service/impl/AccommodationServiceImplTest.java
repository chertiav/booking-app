package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Accommodation.Type.APARTMENT;
import static com.chertiavdev.bookingapp.model.Role.RoleName.USER;
import static com.chertiavdev.bookingapp.util.helpers.NotificationUtils.accommodationCreatedNotification;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_NOT_FOUND_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_UPDATE_ERROR_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TEST_AVAILABILITY_THRESHOLD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.accommodationFromRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createPage;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSampleAccommodationRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.generateAccommodationExistsMessage;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapAccommodationToDto;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.exception.AccommodationAlreadyExistsException;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.AccommodationMapper;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Accommodation.Type;
import com.chertiavdev.bookingapp.repository.accommodation.AccommodationRepository;
import com.chertiavdev.bookingapp.service.NotificationService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Accommodation Service Implementation Test")
class AccommodationServiceImplTest {
    private static final String CITY_BERLIN = "Berlin";
    @InjectMocks
    private AccommodationServiceImpl accommodationService;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private NotificationService notificationService;

    @ParameterizedTest(name = "Case {index}: {1}")
    @MethodSource("validAccommodationProvider")
    @DisplayName("Save accommodation successfully when valid data is provided")
    void save_ValidData_ShouldReturnSavedAccommodation(
            CreateAccommodationRequestDto requestDto,
            String caseName,
            Accommodation accommodationToModel,
            Accommodation savedAccommodation,
            AccommodationDto expected
    ) {
        //Given
        String city = requestDto.getLocation().getCity();
        String street = requestDto.getLocation().getStreet();
        String houseNumber = requestDto.getLocation().getHouseNumber();
        String apartmentNumber = requestDto.getLocation().getApartmentNumber();
        Type type = requestDto.getType();
        String size = requestDto.getSize();

        when(accommodationRepository.existsByLocationAndTypeAndSize(
                city, street, houseNumber, apartmentNumber, type, size))
                .thenReturn(false);
        when(accommodationMapper.toModel(requestDto)).thenReturn(accommodationToModel);
        when(accommodationRepository.save(accommodationToModel)).thenReturn(savedAccommodation);
        when(accommodationMapper.toDto(savedAccommodation)).thenReturn(expected);

        //When
        AccommodationDto actual = accommodationService.save(requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(accommodationMapper).toModel(requestDto);
        verify(accommodationRepository).existsByLocationAndTypeAndSize(
                city, street, houseNumber, apartmentNumber, type, size);
        verify(accommodationRepository).save(accommodationToModel);
        verify(accommodationMapper).toDto(savedAccommodation);
        verify(notificationService).sendNotification(
                accommodationCreatedNotification(expected), USER);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper, notificationService);
    }

    @Test
    @DisplayName("Throw exception when duplicate accommodation is saved")
    void save_DuplicateLocation_ShouldReturnException() {
        //Given
        CreateAccommodationRequestDto requestDto = createSampleAccommodationRequest();
        String city = requestDto.getLocation().getCity();
        String street = requestDto.getLocation().getStreet();
        String houseNumber = requestDto.getLocation().getHouseNumber();
        String apartmentNumber = requestDto.getLocation().getApartmentNumber();
        Type type = requestDto.getType();
        String size = requestDto.getSize();

        when(accommodationRepository.existsByLocationAndTypeAndSize(
                city, street, houseNumber, apartmentNumber, type, size
        )).thenReturn(true);

        //When
        Exception exception = assertThrows(AccommodationAlreadyExistsException.class,
                () -> accommodationService.save(requestDto));

        //Then
        String expected = generateAccommodationExistsMessage(requestDto);
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(accommodationRepository).existsByLocationAndTypeAndSize(
                city, street, houseNumber, apartmentNumber, type, size);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    @DisplayName("Find all accommodations")
    void findAllAvailable_ValidPageable_ShouldReturnPageOfAccommodationDto() {
        //Given
        Accommodation accommodation = accommodationFromRequestDto(
                createSampleAccommodationRequest());
        accommodation.setId(SAMPLE_TEST_ID_1);
        AccommodationDto accommodationDto = mapAccommodationToDto(accommodation);
        Pageable pageable = PageRequest.of(0, 20);

        Page<Accommodation> accommodationPage = createPage(List.of(accommodation), pageable);

        when(accommodationRepository.findAllByAvailabilityGreaterThan(
                TEST_AVAILABILITY_THRESHOLD, pageable)).thenReturn(accommodationPage);
        when(accommodationMapper.toDto(accommodation)).thenReturn(accommodationDto);

        //When
        Page<AccommodationDto> actual = accommodationService.findAllAvailable(pageable);

        //Then
        Page<AccommodationDto> expected = createPage(List.of(accommodationDto), pageable);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getNumberOfElements(),
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

        verify(accommodationRepository).findAllByAvailabilityGreaterThan(
                TEST_AVAILABILITY_THRESHOLD, pageable);
        verify(accommodationMapper).toDto(accommodation);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Find all accommodations when repository returns empty page")
    void findAllAvailable_WhenRepositoryReturnsEmptyPage_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Accommodation> accommodationPage = createPage(List.of(), pageable);
        Page<AccommodationDto> expected = createPage(List.of(), pageable);

        when(accommodationRepository.findAllByAvailabilityGreaterThan(
                TEST_AVAILABILITY_THRESHOLD, pageable)).thenReturn(accommodationPage);

        //When
        Page<AccommodationDto> actual = accommodationService.findAllAvailable(pageable);

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

        verify(accommodationRepository).findAllByAvailabilityGreaterThan(
                TEST_AVAILABILITY_THRESHOLD, pageable);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    @DisplayName("Find by Id should return AccommodationDto when a valid ID is provided")
    void findAvailableById_ValidId_ShouldReturnCategoryDto() {
        //Given
        Accommodation accommodation = accommodationFromRequestDto(
                createSampleAccommodationRequest());
        accommodation.setId(SAMPLE_TEST_ID_1);
        AccommodationDto expected = mapAccommodationToDto(accommodation);

        when(accommodationRepository.findByIdAndAvailabilityGreaterThan(
                SAMPLE_TEST_ID_1,
                TEST_AVAILABILITY_THRESHOLD)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        //When
        AccommodationDto actual = accommodationService.findAvailableById(SAMPLE_TEST_ID_1);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(accommodationRepository).findByIdAndAvailabilityGreaterThan(
                SAMPLE_TEST_ID_1,
                TEST_AVAILABILITY_THRESHOLD);
        verify(accommodationMapper).toDto(accommodation);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Find by Id should throw exception when the ID is invalid")
    void findAvailableById_InvalidId_ShouldReturnException() {
        //Given
        when(accommodationRepository
                .findByIdAndAvailabilityGreaterThan(SAMPLE_TEST_ID_1, TEST_AVAILABILITY_THRESHOLD))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.findAvailableById(SAMPLE_TEST_ID_1));

        //Then
        String expected = ACCOMMODATION_NOT_FOUND_MESSAGE + SAMPLE_TEST_ID_1;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(accommodationRepository).findByIdAndAvailabilityGreaterThan(
                SAMPLE_TEST_ID_1, TEST_AVAILABILITY_THRESHOLD);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    @DisplayName("Update accommodation successfully when valid data is provided")
    void updateById_ValidId_ShouldReturnCategoryDto() {
        //Given
        Accommodation accommodation = accommodationFromRequestDto(
                createSampleAccommodationRequest());
        accommodation.setId(SAMPLE_TEST_ID_1);

        CreateAccommodationRequestDto requestDto = createSampleAccommodationRequest();
        requestDto.setType(APARTMENT);

        AccommodationDto expected = mapAccommodationToDto(accommodation);
        expected.setType(APARTMENT);

        when(accommodationRepository.existsByLocationAndTypeAndSize(
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize()
        )).thenReturn(false);
        when(accommodationRepository.findById(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.of(accommodation));
        doAnswer(invocation -> {
            Accommodation updatedAccommodation = invocation.getArgument(1);
            updatedAccommodation.setType(APARTMENT);
            return null;
        }).when(accommodationMapper).updateAccommodationFromDto(requestDto, accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        //When
        AccommodationDto actual = accommodationService.updateById(SAMPLE_TEST_ID_1, requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(accommodationRepository).existsByLocationAndTypeAndSize(
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize()
        );
        verify(accommodationRepository).findById(accommodation.getId());
        verify(accommodationMapper).updateAccommodationFromDto(requestDto, accommodation);
        verify(accommodationRepository).save(accommodation);
        verify(accommodationMapper).toDto(accommodation);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Update accommodation should throw exception when the ID is invalid")
    void updateById_InvalidId_ShouldReturnException() {
        //Given
        CreateAccommodationRequestDto requestDto = createSampleAccommodationRequest();

        when(accommodationRepository.existsByLocationAndTypeAndSize(
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize()
        )).thenReturn(false);
        when(accommodationRepository
                .findById(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.updateById(SAMPLE_TEST_ID_1, requestDto));

        //Then
        String expected = ACCOMMODATION_UPDATE_ERROR_MESSAGE + SAMPLE_TEST_ID_1;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(accommodationRepository).existsByLocationAndTypeAndSize(
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize()
        );
        verify(accommodationRepository).findById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    @DisplayName("Should delete accommodation successfully when valid ID is provided")
    void deleteById() {
        // Given
        doNothing().when(accommodationRepository).deleteById(SAMPLE_TEST_ID_1);

        // When
        assertDoesNotThrow(() -> accommodationService.deleteById(SAMPLE_TEST_ID_1));

        // Then
        verify(accommodationRepository).deleteById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(accommodationRepository);
    }

    private static Stream<Arguments> validAccommodationProvider() {
        CreateAccommodationRequestDto request1 = createSampleAccommodationRequest();
        Accommodation saved1 = accommodationFromRequestDto(request1);
        saved1.setId(SAMPLE_TEST_ID_1);
        AccommodationDto dto1 = mapAccommodationToDto(saved1);
        Accommodation model1 = accommodationFromRequestDto(request1);

        CreateAccommodationRequestDto request2 = createSampleAccommodationRequest();
        request2.getLocation().setCity(CITY_BERLIN);
        Accommodation saved2 = accommodationFromRequestDto(request2);
        saved2.setId(SAMPLE_TEST_ID_2);
        AccommodationDto dto2 = mapAccommodationToDto(saved2);
        Accommodation model2 = accommodationFromRequestDto(request2);

        return Stream.of(
                arguments(request1, "Default Sample", model1, saved1, dto1),
                arguments(request2, "Different City", model2, saved2, dto2)
        );
    }
}
