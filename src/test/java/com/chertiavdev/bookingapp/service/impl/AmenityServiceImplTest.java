package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_NOT_FOUND_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_UPDATED_NAME;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_UPDATE_ERROR_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.data.builders.AmenityCategoryTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.AmenityTestDataBuilder;
import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.AmenityMapper;
import com.chertiavdev.bookingapp.model.Amenity;
import com.chertiavdev.bookingapp.repository.amenity.AmenityRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Amenity Service Implementation Test")
class AmenityServiceImplTest {
    private AmenityTestDataBuilder amenityTestDataBuilder;
    @InjectMocks
    private AmenityServiceImpl amenityService;
    @Mock
    private AmenityMapper amenityMapper;
    @Mock
    private AmenityRepository amenityRepository;

    @BeforeEach
    void setUp() {
        amenityTestDataBuilder = new AmenityTestDataBuilder(
                new AmenityCategoryTestDataBuilder()
        );
    }

    @Test
    @DisplayName("Save an amenity successfully when valid data is provided")
    void save() {
        //Given
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder.getAmenityFreeWiFiRequestDto();
        Amenity amenityModel = amenityTestDataBuilder.getAmenityFreeWiFiToModel();
        Amenity amenity = amenityTestDataBuilder.getAmenityFreeWiFi();
        AmenityDto expected = amenityTestDataBuilder.getAmenityFreeWiFiDto();

        when(amenityMapper.toModel(requestDto)).thenReturn(amenityModel);
        when(amenityRepository.save(amenityModel)).thenReturn(amenity);
        when(amenityMapper.toDto(amenity)).thenReturn(expected);

        //When
        AmenityDto actual = amenityService.save(requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityMapper).toModel(requestDto);
        verify(amenityRepository).save(amenityModel);
        verify(amenityMapper).toDto(amenity);
        verifyNoMoreInteractions(amenityRepository, amenityMapper);
    }

    @Test
    @DisplayName("Find all amenities")
    void findAll_Valid_ShouldReturnListOfOfAmenityDto() {
        //Given
        Amenity amenity = amenityTestDataBuilder.getAmenityFreeWiFi();
        AmenityDto amenityDto = amenityTestDataBuilder.getAmenityFreeWiFiDto();
        List<Amenity> amenityList = List.of(amenity);

        when(amenityRepository.findAll()).thenReturn(amenityList);
        when(amenityMapper.toDto(amenity)).thenReturn(amenityDto);

        //When
        List<AmenityDto> actual = amenityService.findAll();

        //Then
        List<AmenityDto> expected = List.of(amenityDto);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityRepository).findAll();
        verify(amenityMapper).toDto(amenity);
        verifyNoMoreInteractions(amenityRepository, amenityMapper);
    }

    @Test
    @DisplayName("Find by Id should return AmenityDto when a valid ID is provided")
    void findById_ValidId_ShouldReturnAmenityDto() {
        //Given
        Amenity amenity = amenityTestDataBuilder.getAmenityFreeWiFi();
        AmenityDto expected = amenityTestDataBuilder.getAmenityFreeWiFiDto();

        when(amenityRepository.findById(amenity.getId())).thenReturn(Optional.of(amenity));
        when(amenityMapper.toDto(amenity)).thenReturn(expected);

        //When
        AmenityDto actual = amenityService.findById(amenity.getId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityRepository).findById(amenity.getId());
        verify(amenityMapper).toDto(amenity);
        verifyNoMoreInteractions(amenityRepository, amenityMapper);
    }

    @Test
    @DisplayName("Find by Id should throw exception when the ID is invalid")
    void findById_InvalidId_ShouldReturnException() {
        //Given
        when(amenityRepository.findById(INVALID_TEST_ID))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> amenityService.findById(INVALID_TEST_ID));

        //Then
        String expected = AMENITY_NOT_FOUND_MESSAGE + INVALID_TEST_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityRepository).findById(INVALID_TEST_ID);
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("Update Amenity successfully when valid data is provided")
    void updateById_ValidId_ShouldReturnUpdatedAmenityDto() {
        //Given
        Amenity amenity = amenityTestDataBuilder.getAmenityFreeWiFi();
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder.getAmenityFreeWiFiRequestDto();
        AmenityDto expected = amenityTestDataBuilder.getUpdatedAmenityFreeWiFiDto();

        when(amenityRepository.findById(amenity.getId()))
                .thenReturn(Optional.of(amenity));
        doAnswer(invocation -> {
            Amenity updatedAmenity = invocation.getArgument(1);
            updatedAmenity.setName(AMENITY_UPDATED_NAME);
            return null;
        }).when(amenityMapper).update(requestDto, amenity);
        when(amenityRepository.save(amenity)).thenReturn(amenity);
        when(amenityMapper.toDto(amenity)).thenReturn(expected);

        //When
        AmenityDto actual = amenityService.updateById(amenity.getId(), requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityRepository).findById(amenity.getId());
        verify(amenityMapper).update(requestDto, amenity);
        verify(amenityRepository).save(amenity);
        verify(amenityMapper).toDto(amenity);
        verifyNoMoreInteractions(amenityRepository, amenityMapper);
    }

    @Test
    @DisplayName("Update Amenity should throw exception when the ID is invalid")
    void updateById_InvalidId_ShouldReturnException() {
        //Given
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder.getAmenityFreeWiFiRequestDto();

        when(amenityRepository.findById(INVALID_TEST_ID))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> amenityService.updateById(INVALID_TEST_ID, requestDto));

        //Then
        String expected = AMENITY_UPDATE_ERROR_MESSAGE + INVALID_TEST_ID;
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityRepository).findById(INVALID_TEST_ID);
        verifyNoMoreInteractions(amenityRepository);
    }

    @Test
    @DisplayName("Should delete Amenity successfully when valid ID is provided")
    void deleteById_ValidId_ShouldDeleteAmenity() {
        //Given
        doNothing().when(amenityRepository).deleteById(SAMPLE_TEST_ID_1);

        //When
        assertDoesNotThrow(() -> amenityService.deleteById(SAMPLE_TEST_ID_1));

        //Then
        verify(amenityRepository).deleteById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(amenityRepository);
    }
}
