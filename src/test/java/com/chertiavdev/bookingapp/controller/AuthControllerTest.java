package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AUTH_LOGIN_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AUTH_REGISTER_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_BAD_CREDENTIALS;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_INVALID_EMAIL_FORMAT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_USER_ALREADY_EXISTS;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_PASSWORD_DO_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXPIRATION_SHOULD_BE_IN_THE_FUTURE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXPIRATION_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_EMAIL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_REGISTER_DTO;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ISSUED_AT_SHOULD_BE_IN_THE_PAST;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ISSUED_AT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TIMESTAMP_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOKEN_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USERNAME_IN_THE_TOKEN_SHOULD_MATCH_THE_LOGIN_EMAIL;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorDetailMap;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseErrorResponseFromMvcResult;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserLoginRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserLoginResponseDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("AuthControllerTest Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class AuthControllerTest {
    protected static MockMvc mockMvc;
    private static final String[] SETUP_SCRIPTS = {
            "database/user/add-users-to-users-table.sql",
            "database/user/role/add-role-for-into-users_roles_table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/user/role/remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
            "database/user/remove-users-where-id-more-than-one-from-users-table.sql"
    };
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserTestDataBuilder userTestDataBuilder;
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        setupDatabase(dataSource);
    }

    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, SETUP_SCRIPTS);
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, CLEANUP_SCRIPTS);
        }
    }

    @DisplayName("Register a user should return UserDto when a valid data is provided")
    @Sql(
            scripts = {
                    "classpath:database/user/role/"
                            + "remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
                    "classpath:database/user/"
                            + "remove-users-where-id-more-than-one-from-users-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    void register_ValidData_ShouldReturnUserDto() throws Exception {
        //Given
        UserRegisterRequestDto requestDto = userTestDataBuilder.getUserJohnRegisterRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        UserDto expected = userTestDataBuilder.getUserJohnDto();

        //When
        MvcResult result = mockMvc
                .perform(post(AUTH_REGISTER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                UserDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @DisplayName("Registering a user when an email already exists "
            + "should throw a RegistrationException")
    @Test
    void register_ExistedEmail_ShouldThrowRegistrationException() throws Exception {
        //Given
        UserRegisterRequestDto requestDto = userTestDataBuilder.getUserJohnRegisterRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                String.format(ERROR_MESSAGE_USER_ALREADY_EXISTS, requestDto.getEmail())
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AUTH_REGISTER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @DisplayName("Registering a user when an invalid repeat password is provided "
            + "should throw a BadRequest")
    @Test
    void register_InvalidRepeatPasswordData_ShouldThrowBadRequest() throws Exception {
        //Given
        UserRegisterRequestDto requestDto = userTestDataBuilder.getUserJohnRegisterBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_REGISTER_DTO,
                ERROR_PASSWORD_DO_NOT_MATCH
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AUTH_REGISTER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @DisplayName("Logging a user when valid data is provided "
            + "should return UserLoginResponseDto")
    @Test
    void login_ValidData_ShouldReturnUserLoginResponseDto() throws Exception {
        //Given
        UserLoginRequestDto requestDto = userTestDataBuilder
                .createUserJohnLoginRequestDto(true, true);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc
                .perform(post(AUTH_LOGIN_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                UserLoginResponseDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertNotNull(actual.token(), TOKEN_SHOULD_NOT_BE_NULL);

        Claims claims = jwtUtil.parseClaims(actual.token());

        assertEquals(requestDto.getEmail(), claims.getSubject(),
                USERNAME_IN_THE_TOKEN_SHOULD_MATCH_THE_LOGIN_EMAIL);
        assertNotNull(claims.getIssuedAt(), ISSUED_AT_SHOULD_NOT_BE_NULL);
        assertTrue(claims.getIssuedAt().before(new Date()), ISSUED_AT_SHOULD_BE_IN_THE_PAST);
        assertNotNull(claims.getExpiration(), EXPIRATION_SHOULD_NOT_BE_NULL);
        assertTrue(claims.getExpiration().after(new Date()), EXPIRATION_SHOULD_BE_IN_THE_FUTURE);
    }

    @DisplayName("Logging a user when invalid password is provided "
            + "should throw Unauthorized")
    @Test
    void login_InvalidPassword_ShouldThrowUnauthorized() throws Exception {
        //Given
        UserLoginRequestDto requestDto = userTestDataBuilder
                .createUserJohnLoginRequestDto(true, false);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_BAD_CREDENTIALS
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AUTH_LOGIN_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @DisplayName("Logging a user when an invalid email format is provided "
            + "should throw BadRequest")
    @Test
    void login_InvalidEmailFormat_ShouldThrowBadRequest() throws Exception {
        //Given
        UserLoginRequestDto requestDto = userTestDataBuilder
                .createUserJohnLoginRequestDto(false, true);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_EMAIL,
                ERROR_INVALID_EMAIL_FORMAT
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AUTH_LOGIN_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }
}
