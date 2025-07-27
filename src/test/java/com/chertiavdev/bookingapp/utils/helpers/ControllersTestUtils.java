package com.chertiavdev.bookingapp.utils.helpers;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_FIELD_TITLE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_TITLE;

import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public final class ControllersTestUtils {

    private ControllersTestUtils() {
    }

    public static CommonApiErrorResponseDto createErrorResponse(HttpStatus status, Object message) {
        return new CommonApiErrorResponseDto(
                status,
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                message
        );
    }

    public static CommonApiErrorResponseDto parseErrorResponseFromMvcResult(
            MvcResult result,
            ObjectMapper objectMapper
    )
            throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CommonApiErrorResponseDto.class);
    }

    public static Map<String, String> createErrorDetailMap(
            String errorFieldTitle,
            String errorMessageTitle
    ) {
        Map<String, String> errorDetailDto = new LinkedHashMap<>();
        errorDetailDto.put(ERROR_FIELD_TITLE, errorFieldTitle);
        errorDetailDto.put(ERROR_MESSAGE_TITLE, errorMessageTitle);
        return errorDetailDto;
    }

    public static <T> T mapMvcResultToObjectDto(
            MvcResult result,
            ObjectMapper objectMapper,
            Class<T> clazz
    ) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
    }

    public static <T> List<T> parseObjectDtoToList(
            MvcResult result,
            ObjectMapper objectMapper,
            Class<T> clazz
    ) throws IOException {
        JavaType type = objectMapper
                .getTypeFactory()
                .constructParametricType(List.class, clazz);
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), type);

    }

    public static <T> PageResponse<T> parseObjectDtoPageResponse(
            MvcResult result,
            ObjectMapper objectMapper,
            Class<T> clazz
    ) throws IOException {
        JavaType type = objectMapper
                .getTypeFactory()
                .constructParametricType(PageResponse.class, clazz);
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), type);

    }
}
