package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_NAME_UPDATE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_NOT_FOUND_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_UPDATE_ERROR_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.amenityCategoryFromRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSampleAmenityCategoryRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapAmenityCategorToDto;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.AmenityCategoryMapper;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import com.chertiavdev.bookingapp.repository.amenity.category.AmenityCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Amenity Category Service Implementation Test")
class AmenityCategoryServiceImplTest {
    @InjectMocks
    private AmenityCategoryServiceImpl amenityCategoryService;
    @Mock
    private AmenityCategoryMapper amenityCategoryMapper;
    @Mock
    private AmenityCategoryRepository amenityCategoryRepository;

    @Test
    @DisplayName("Save category of amenities successfully when valid data is provided")
    void save_ValidData_ShouldReturnSavedAmenityCategoryDto() {
        //Given
        CreateAmenityCategoryRequestDto requestDto = createSampleAmenityCategoryRequest();
        AmenityCategory amenityCategoryModel = amenityCategoryFromRequestDto(requestDto);

        AmenityCategory amenityCategory = amenityCategoryFromRequestDto(requestDto);
        amenityCategory.setId(SAMPLE_TEST_ID_1);

        AmenityCategoryDto expected = mapAmenityCategorToDto(amenityCategory);
        when(amenityCategoryMapper.toModel(requestDto)).thenReturn(amenityCategoryModel);
        when(amenityCategoryRepository.save(amenityCategoryModel)).thenReturn(amenityCategory);
        when(amenityCategoryMapper.toDto(amenityCategory)).thenReturn(expected);

        //When
        AmenityCategoryDto actual = amenityCategoryService.save(requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityCategoryMapper).toModel(requestDto);
        verify(amenityCategoryRepository).save(amenityCategoryModel);
        verify(amenityCategoryMapper).toDto(amenityCategory);
        verifyNoMoreInteractions(amenityCategoryRepository, amenityCategoryMapper);
    }

    @Test
    @DisplayName("Find all categories of amenity")
    void findAll_Valid_ShouldReturnListOfAmenityCategoryDto() {
        //Given
        AmenityCategory amenityCategory = amenityCategoryFromRequestDto(
                createSampleAmenityCategoryRequest());
        amenityCategory.setId(SAMPLE_TEST_ID_1);

        AmenityCategoryDto amenityCategoryDto = mapAmenityCategorToDto(amenityCategory);
        List<AmenityCategory> amenityCategoryList = List.of(amenityCategory);

        when(amenityCategoryRepository.findAll()).thenReturn(amenityCategoryList);
        when(amenityCategoryMapper.toDto(amenityCategory)).thenReturn(amenityCategoryDto);

        //When
        List<AmenityCategoryDto> actual = amenityCategoryService.findAll();

        //Then
        List<AmenityCategoryDto> expected = List.of(amenityCategoryDto);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityCategoryRepository).findAll();
        verify(amenityCategoryMapper).toDto(amenityCategory);
        verifyNoMoreInteractions(amenityCategoryRepository, amenityCategoryMapper);
    }

    @Test
    @DisplayName("Find by Id should return AmenityCategoryDto when a valid ID is provided")
    void findById_ValidId_ShouldReturnAmenityCategoryDto() {
        //Given
        AmenityCategory amenityCategory = amenityCategoryFromRequestDto(
                createSampleAmenityCategoryRequest()
        );
        amenityCategory.setId(SAMPLE_TEST_ID_1);
        AmenityCategoryDto expected = mapAmenityCategorToDto(amenityCategory);

        when(amenityCategoryRepository.findById(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.of(amenityCategory));
        when(amenityCategoryMapper.toDto(amenityCategory)).thenReturn(expected);

        //When
        AmenityCategoryDto actual = amenityCategoryService.findById(SAMPLE_TEST_ID_1);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityCategoryRepository).findById(SAMPLE_TEST_ID_1);
        verify(amenityCategoryMapper).toDto(amenityCategory);
        verifyNoMoreInteractions(amenityCategoryRepository, amenityCategoryMapper);
    }

    @Test
    @DisplayName("Find by Id should throw exception when the ID is invalid")
    void findById_InvalidId_ShouldReturnException() {
        //Given
        when(amenityCategoryRepository.findById(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> amenityCategoryService.findById(SAMPLE_TEST_ID_1));

        //Then
        String expected = AMENITY_CATEGORY_NOT_FOUND_MESSAGE + SAMPLE_TEST_ID_1;
        String actual = exception.getMessage();

        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityCategoryRepository).findById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(amenityCategoryRepository);
    }

    @Test
    @DisplayName("Update AmenityCategory successfully when valid data is provided")
    void updateById_ValidId_ShouldReturnAmenityCategoryDto() {
        //Given
        AmenityCategory amenityCategory = amenityCategoryFromRequestDto(
                createSampleAmenityCategoryRequest()
        );
        amenityCategory.setId(SAMPLE_TEST_ID_1);

        CreateAmenityCategoryRequestDto requestDto = createSampleAmenityCategoryRequest();
        requestDto.setName(AMENITY_CATEGORY_NAME_UPDATE);

        AmenityCategoryDto expected = mapAmenityCategorToDto(amenityCategory);
        expected.setName(AMENITY_CATEGORY_NAME_UPDATE);

        when(amenityCategoryRepository.findById(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.of(amenityCategory));
        doAnswer(invocation -> {
            AmenityCategory updatedAmenityCategory = invocation.getArgument(1);
            updatedAmenityCategory.setName(AMENITY_CATEGORY_NAME_UPDATE);
            return null;
        }).when(amenityCategoryMapper).updateFromDto(requestDto, amenityCategory);
        when(amenityCategoryRepository.save(amenityCategory)).thenReturn(amenityCategory);
        when(amenityCategoryMapper.toDto(amenityCategory)).thenReturn(expected);

        //When
        AmenityCategoryDto actual = amenityCategoryService.updateById(SAMPLE_TEST_ID_1, requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityCategoryRepository).findById(SAMPLE_TEST_ID_1);
        verify(amenityCategoryMapper).updateFromDto(requestDto, amenityCategory);
        verify(amenityCategoryRepository).save(amenityCategory);
        verify(amenityCategoryMapper).toDto(amenityCategory);
        verifyNoMoreInteractions(amenityCategoryRepository, amenityCategoryMapper);
    }

    @Test
    @DisplayName("Update AmenityCategory should throw exception when the ID is invalid")
    void updateById_InvalidId_ShouldReturnException() {
        //Given
        CreateAmenityCategoryRequestDto requestDto = createSampleAmenityCategoryRequest();

        when(amenityCategoryRepository.findById(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> amenityCategoryService.updateById(SAMPLE_TEST_ID_1, requestDto));

        //Then
        String expected = AMENITY_CATEGORY_UPDATE_ERROR_MESSAGE + SAMPLE_TEST_ID_1;
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(amenityCategoryRepository).findById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(amenityCategoryRepository);
    }

    @Test
    @DisplayName("Should delete AmenityCategory successfully when valid ID is provided")
    void deleteById_ValidId_ShouldDeleteAmenityCategory() {
        //Given
        doNothing().when(amenityCategoryRepository).deleteById(SAMPLE_TEST_ID_1);

        //When
        assertDoesNotThrow(() -> amenityCategoryService.deleteById(SAMPLE_TEST_ID_1));

        //Then
        verify(amenityCategoryRepository).deleteById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(amenityCategoryRepository);
    }
}
