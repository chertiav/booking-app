package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_FIRST_NAME_REQUIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_USER_NOT_FOUND_EMAIL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_USER_NOT_FOUND_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_ROLE_MUST_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_FIRST_NAME;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_ROLE_NAME;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_ROLE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TIMESTAMP_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USERS_ME_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USERS_UPDATE_ROLE_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_EMAIL_JOHN;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_INVALID_EMAIL;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorDetailMap;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.mapMvcResultToObjectDto;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseErrorResponseFromMvcResult;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("User Controller Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class UserControllerTest {
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

    @BeforeEach
    void setUp(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        setupDatabase(dataSource);
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, SETUP_SCRIPTS);
        }
    }

    @SneakyThrows
    private void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, CLEANUP_SCRIPTS);
        }
    }

    @WithMockUser(username = USER_EMAIL_JOHN)
    @DisplayName("Finding a user by email should return UserDto when a valid email is provided")
    @Test
    void findByEmail_ValidEmail_ShouldReturnUserDto() throws Exception {
        //Given
        UserDto expected = userTestDataBuilder.getUserJohnDto();

        //When
        MvcResult result = mockMvc.perform(get(USERS_ME_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserDto actual = mapMvcResultToObjectDto(result, objectMapper, UserDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = USER_EMAIL_JOHN, roles = {INVALID_ROLE})
    @DisplayName("Finding a user by email when an invalid role is provided should return forbidden")
    @Test
    void findByEmail_InValidRole_ShouldReturnForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(USERS_ME_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
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

    @WithMockUser(username = USER_INVALID_EMAIL)
    @DisplayName("Finding a user by email when an invalid email is provided "
            + "should return NotFound")
    @Test
    void findByEmail_InValidEmail_ShouldReturnNotFound() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_USER_NOT_FOUND_EMAIL + USER_INVALID_EMAIL
        );

        //When
        MvcResult result = mockMvc
                .perform(get(USERS_ME_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
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

    @WithMockUser(username = USER_EMAIL_JOHN)
    @DisplayName("Updating a user by email successfully valid data is provided")
    @Test
    void updateByEmail_ValidData_ShouldReturnUserDto() throws Exception {
        //Given
        UserUpdateRequestDto requestDto = userTestDataBuilder.getUpdatedUserJohnRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        UserDto expected = userTestDataBuilder.getUpdatedUserJohnDto();

        //When
        MvcResult result = mockMvc
                .perform(put(USERS_ME_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserDto actual = mapMvcResultToObjectDto(result, objectMapper, UserDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = USER_EMAIL_JOHN)
    @DisplayName("Updating a user by email when invalid data is provided")
    @Test
    void updateByEmail_InvalidData_ShouldReturnBadRequest() throws Exception {
        //Given
        UserUpdateRequestDto requestDto = userTestDataBuilder.getUpdatedUserJohnBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_FIRST_NAME,
                ERROR_FIRST_NAME_REQUIRED
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(put(USERS_ME_ENDPOINT)
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

    @WithMockUser(username = USER_EMAIL_JOHN, roles = {INVALID_ROLE})
    @DisplayName("Updating a user by email when invalid users role data is provided")
    @Test
    void updateByEmail_InvalidRole_ShouldReturnForbidden() throws Exception {
        //Given
        UserUpdateRequestDto requestDto = userTestDataBuilder.getUpdatedUserJohnRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(USERS_ME_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
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

    @WithMockUser(username = USER_INVALID_EMAIL)
    @DisplayName("Updating a user by email when invalid users role data is provided")
    @Test
    void updateByEmail_InvalidEmail_ShouldReturnNotFound() throws Exception {
        //Given
        UserUpdateRequestDto requestDto = userTestDataBuilder.getUpdatedUserJohnRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_USER_NOT_FOUND_EMAIL + USER_INVALID_EMAIL
        );

        //When
        MvcResult result = mockMvc
                .perform(put(USERS_ME_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Updating a users role successfully valid data is provided")
    @Test
    void updateRole_ValidData_ShouldReturnUserWithRoleDto() throws Exception {
        //Given
        Long updatedUserId = userTestDataBuilder.getUserJohn().getId();
        UserUpdateRoleRequestDto requestDto = userTestDataBuilder.getUserJohnUpdateRoleRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        UserWithRoleDto expected = userTestDataBuilder.getUpdatedRoleUserJohnDto();

        //When
        MvcResult result = mockMvc
                .perform(patch(String.format(USERS_UPDATE_ROLE_ENDPOINT, updatedUserId))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserWithRoleDto actual = mapMvcResultToObjectDto(
                result, objectMapper, UserWithRoleDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Updating a users role when role is null")
    @Test
    void updateRole_RoleNull_ShouldReturnBadRequest() throws Exception {
        //Given
        Long updatedUserId = userTestDataBuilder.getUserJohn().getId();
        UserUpdateRoleRequestDto requestDto = userTestDataBuilder
                .getUserJohnUpdateRoleBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_ROLE_NAME,
                ERROR_ROLE_MUST_NOT_BE_NULL
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(patch(String.format(USERS_UPDATE_ROLE_ENDPOINT, updatedUserId))
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

    @WithMockUser(username = "user")
    @DisplayName("Updating a users role when denied acces")
    @Test
    void updateRole_DeniedAccess_ShouldReturnForbidden() throws Exception {
        //Given
        Long updatedUserId = userTestDataBuilder.getUserJohn().getId();
        UserUpdateRoleRequestDto requestDto = userTestDataBuilder
                .getUserJohnUpdateRoleRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(patch(String.format(USERS_UPDATE_ROLE_ENDPOINT, updatedUserId))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Updating a users role when invalid userId is provided")
    @Test
    void updateRole_InvalidUserId_ShouldReturnNotFound() throws Exception {
        //Given
        UserUpdateRoleRequestDto requestDto = userTestDataBuilder.getUserJohnUpdateRoleRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_USER_NOT_FOUND_ID + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(patch(String.format(USERS_UPDATE_ROLE_ENDPOINT, INVALID_TEST_ID))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
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
