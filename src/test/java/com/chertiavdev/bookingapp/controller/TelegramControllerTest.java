package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_ROLE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_BE_DELETED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_EXIST_BEFORE_DELETION;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TELEGRAM_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TELEGRAM_LINK_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TELEGRAM_LINK_STATUS_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TIMESTAMP_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_EMAIL_JOHN;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_EMAIL_SANSA;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_INVALID_EMAIL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_TELEGRAM_TABLE_NAME;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.mapMvcResultToObjectDto;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseErrorResponseFromMvcResult;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.recordExistsInDatabaseById;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.config.UuidTokenGeneratorTestConfig;
import com.chertiavdev.bookingapp.data.builders.UserTelegramLinkTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTelegramTestDataBuilder;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkDto;
import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Telegram Controller Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestConfig.class, UuidTokenGeneratorTestConfig.class})
class TelegramControllerTest {
    protected static MockMvc mockMvc;
    private static final String[] SETUP_SCRIPTS = {
            "database/user/add-users-to-users-table.sql",
            "database/user/role/add-role-for-into-users_roles_table.sql",
            "database/telegram/add-telegram_link-to-telegram_links-table.sql",
            "database/user/telegram/add-user_telegram-to-user_telegram-table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/telegram/remove-all-telegram_links-from-telegram_links-table.sql",
            "database/user/telegram/remove-all-from-user_telegram.sql",
            "database/user/role/remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
            "database/user/remove-users-where-id-more-than-one-from-users-table.sql",
    };
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserTelegramLinkTestDataBuilder userTelegramLinkTestDataBuilder;
    @Autowired
    private UserTelegramTestDataBuilder userTelegramTestDataBuilder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setUp(
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
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, SETUP_SCRIPTS);
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, CLEANUP_SCRIPTS);
        }
    }

    @Sql(
            scripts = {
                    "classpath:database/telegram/"
                            + "remove-all-telegram_links-from-telegram_links-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/telegram/"
                            + "remove-all-telegram_links-from-telegram_links-table.sql",
                    "classpath:database/telegram/add-telegram_link-to-telegram_links-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Creating a telegram link when a valid data is provided "
            + "should return TelegramLinkDto")
    @Test
    void createLink_ValidData_ShouldReturnTelegramLinkDto() throws Exception {
        //Given
        TelegramLinkDto expected = userTelegramLinkTestDataBuilder
                .getTelegramLinkDtoJohn();

        //When
        MvcResult result = mockMvc
                .perform(post(TELEGRAM_ENDPOINT + TELEGRAM_LINK_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        TelegramLinkDto actual = mapMvcResultToObjectDto(
                result, objectMapper, TelegramLinkDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = USER_INVALID_EMAIL, roles = {INVALID_ROLE})
    @DisplayName("Creating a telegram link when an invalid user's role is provided "
            + "should throw Forbidden")
    @Test
    void createLink_InValidRole_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(TELEGRAM_ENDPOINT + TELEGRAM_LINK_ENDPOINT)
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

    @DisplayName("Creating a telegram link when an invalid user's role is provided "
            + "should throw Unauthorized")
    @Test
    void createLink_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(TELEGRAM_ENDPOINT + TELEGRAM_LINK_ENDPOINT)
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

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Getting a telegram link status when a user is subscribed to Telegram"
            + "should return UserTelegramStatusDto")
    @Test
    void getStatus_UserHasCurrentLink_ShouldReturnUserTelegramStatusDto() throws Exception {
        //Given
        UserTelegramStatusDto expected = userTelegramTestDataBuilder
                .getUserTelegramStatusDtoJohn();

        //When
        MvcResult result = mockMvc
                .perform(get(TELEGRAM_ENDPOINT + TELEGRAM_LINK_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserTelegramStatusDto actual = mapMvcResultToObjectDto(
                result, objectMapper, UserTelegramStatusDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(value = USER_EMAIL_SANSA)
    @DisplayName("Getting a telegram link status when a user is not subscribed to Telegram"
            + "should return UserTelegramStatusDto with False")
    @Test
    void getStatus_UserHasCurrentLink_ShouldReturnUserTelegramStatusDtoWithFalse(
    ) throws Exception {
        //Given
        UserTelegramStatusDto expected = userTelegramTestDataBuilder
                .getUserTelegramStatusDtoSansa();

        //When
        MvcResult result = mockMvc
                .perform(get(TELEGRAM_ENDPOINT + TELEGRAM_LINK_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserTelegramStatusDto actual = mapMvcResultToObjectDto(
                result, objectMapper, UserTelegramStatusDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = USER_INVALID_EMAIL, roles = {INVALID_ROLE})
    @DisplayName("Getting a telegram link status when an invalid user's role is provided"
            + "should throw Forbidden")
    @Test
    void getStatus_InValidRole_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(TELEGRAM_ENDPOINT + TELEGRAM_LINK_STATUS_ENDPOINT)
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

    @DisplayName("Getting a telegram link status when an user is unauthorized"
            + "should throw Unauthorized")
    @Test
    void getStatus_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(TELEGRAM_ENDPOINT + TELEGRAM_LINK_STATUS_ENDPOINT)
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

    @Sql(
            scripts = {
                    "classpath:database/user/telegram/remove-all-from-user_telegram.sql",
                    "classpath:database/user/telegram/add-user_telegram-to-user_telegram-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Unlinking from a Telegram subscription when a user is subscribed to Telegram"
            + "should return NoContent")
    @Test
    void unlink_UserHasSubscription_ShouldReturnNoContent() throws Exception {
        //Given
        boolean userTelegramExistsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        //When
        mockMvc
                .perform(delete(TELEGRAM_ENDPOINT + TELEGRAM_LINK_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //Given
        boolean userTelegramExistsAfter = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        assertTrue(userTelegramExistsBefore, RECORD_SHOULD_EXIST_BEFORE_DELETION);
        assertFalse(userTelegramExistsAfter, RECORD_SHOULD_BE_DELETED);
    }

    @WithMockUser(username = USER_INVALID_EMAIL, roles = {INVALID_ROLE})
    @DisplayName("Unlinking from a Telegram subscription when an invalid user's role is provided"
            + "should throw Forbidden")
    @Test
    void unlink_InValidRole_ShouldReturnForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(TELEGRAM_ENDPOINT + TELEGRAM_LINK_ENDPOINT)
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

    @DisplayName("Unlinking from a Telegram subscription when an user is unauthorized"
            + "should throw Unauthorized")
    @Test
    void unlink_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(TELEGRAM_ENDPOINT + TELEGRAM_LINK_ENDPOINT)
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
}
