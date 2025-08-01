package com.chertiavdev.bookingapp.repository.user.telegram;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_BE_DELETED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_EXIST_BEFORE_DELETION;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_TELEGRAM_NEW_CHAT_ID;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.recordExistsInDatabaseById;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.UserTelegramTestDataBuilder;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.UserTelegram;
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
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DisplayName("User Telegram Repository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class UserTelegramRepositoryTest {
    private static final String USER_TELEGRAM_TABLE_NAME = "user_telegram";
    private static final String[] SETUP_SCRIPTS = {
            "database/user/add-users-to-users-table.sql",
            "database/user/role/add-role-for-into-users_roles_table.sql",
            "database/user/telegram/add-user_telegram-to-user_telegram-table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/user/telegram/remove-all-from-user_telegram.sql",
            "database/user/role/remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
            "database/user/remove-users-where-id-more-than-one-from-users-table.sql"
    };
    @Autowired
    private UserTelegramRepository userTelegramRepository;
    @Autowired
    private UserTelegramTestDataBuilder userTelegramTestDataBuilder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Test
    @DisplayName("Find a UserTelegram by userId when valid data is provided should return "
            + "an Optional of UserTelegram")
    void findByUserId_ValidUserId_ShouldReturnOptionalOfUserTelegram() {
        //Given
        UserTelegram expected = userTelegramTestDataBuilder.getUserTelegramJohn();

        //When
        Optional<UserTelegram> actual = userTelegramRepository
                .findByUserId(expected.getUser().getId());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find a UserTelegram by userId when invalid userId is provided should return "
            + "an Optional of empty")
    void findByUserId_InValidUserId_ShouldReturnOptionalOfEmpty() {
        //When
        Optional<UserTelegram> actual = userTelegramRepository.findByUserId(INVALID_TEST_ID);

        //Then
        assertFalse(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Restore UserTelegram And Update ChatId when valid data is provided")
    void restoreUserTelegramAndUpdateChatId_ValidData_ShouldRestoreAndUpdate() {
        //Given
        UserTelegram deletedUserTelegram = userTelegramTestDataBuilder
                .getDeletedUserTelegramSansa();
        boolean existsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                deletedUserTelegram.getId()
        );

        //When
        userTelegramRepository.restoreUserTelegramAndUpdateChatId(
                deletedUserTelegram.getId(),
                USER_TELEGRAM_NEW_CHAT_ID
        );

        //Then
        Optional<UserTelegram> actual = userTelegramRepository
                .findById(deletedUserTelegram.getId());

        assertFalse(existsBefore, RECORD_SHOULD_BE_DELETED);
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertNotEquals(USER_TELEGRAM_NEW_CHAT_ID, deletedUserTelegram.getChatId(),
                ACTUAL_RESULT_SHOULD_NOT_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(USER_TELEGRAM_NEW_CHAT_ID, actual.get().getChatId(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Restore UserTelegram And Update ChatId when invalid data is provided")
    void restoreUserTelegramAndUpdateChatId_InvalidData_ShouldNotRestoreAndUpdate() {
        //Given
        Long invalidId = INVALID_TEST_ID;
        boolean existsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                invalidId
        );

        //When
        userTelegramRepository.restoreUserTelegramAndUpdateChatId(
                invalidId,
                USER_TELEGRAM_NEW_CHAT_ID
        );

        //Then
        Optional<UserTelegram> actual = userTelegramRepository.findById(invalidId);

        assertFalse(existsBefore, ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
        assertFalse(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Obtaining a UserTelegram status by userId when valid data is provided")
    void existsByUserId_ValidUserId_ShouldReturnTrue() {
        //Given
        UserTelegram userTelegramJohn = userTelegramTestDataBuilder.getUserTelegramJohn();

        //When
        boolean actual = userTelegramRepository.existsByUserId(userTelegramJohn.getUser().getId());

        //Then
        assertTrue(actual, ACTUAL_RESULT_SHOULD_BE_PRESENT);
    }

    @Test
    @DisplayName("Obtaining a UserTelegram status by userId when invalid data is provided")
    void existsByUserId_InValidUserId_ShouldReturnFalse() {
        //Given
        UserTelegram deletedUserTelegram = userTelegramTestDataBuilder
                .getDeletedUserTelegramSansa();

        //When
        boolean actual = userTelegramRepository
                .existsByUserId(deletedUserTelegram.getUser().getId());

        //Then
        assertFalse(actual, ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Delete a UserTelegram by chatId when valid data is provided")
    void deleteByChatId_ValidChatId_ShouldDelete() {
        //Given
        UserTelegram existUserTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();
        boolean existsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                existUserTelegram.getId()
        );

        //When
        userTelegramRepository.deleteByChatId(existUserTelegram.getChatId());
        userTelegramRepository.flush();

        //Then
        boolean existsAfter = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                existUserTelegram.getId()
        );

        assertTrue(existsBefore, RECORD_SHOULD_EXIST_BEFORE_DELETION);
        assertFalse(existsAfter, RECORD_SHOULD_BE_DELETED);
    }

    @Test
    @DisplayName("Delete a UserTelegram by userId when valid data is provided")
    void deleteByUserId_ValidUserId_ShouldDelete() {
        //Given
        UserTelegram existUserTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();
        boolean existsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                existUserTelegram.getId()
        );

        //When
        userTelegramRepository.deleteByUserId(existUserTelegram.getUser().getId());
        userTelegramRepository.flush();

        //Then
        boolean existsAfter = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                existUserTelegram.getId()
        );

        assertTrue(existsBefore, RECORD_SHOULD_EXIST_BEFORE_DELETION);
        assertFalse(existsAfter, RECORD_SHOULD_BE_DELETED);
    }

    @Test
    @DisplayName("Delete a UserTelegram by userId when invalid data is provided")
    void deleteByChatId_InValidUserId_ShouldNotDelete() {
        //Given
        boolean existsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                INVALID_TEST_ID
        );

        //When
        userTelegramRepository.deleteByUserId(INVALID_TEST_ID);
        userTelegramRepository.flush();

        //Then
        boolean existsAfter = recordExistsInDatabaseById(
                jdbcTemplate,
                USER_TELEGRAM_TABLE_NAME,
                INVALID_TEST_ID
        );

        assertFalse(existsBefore, RECORD_SHOULD_BE_DELETED);
        assertFalse(existsAfter, RECORD_SHOULD_BE_DELETED);
    }

    @Test
    @DisplayName("Find all by users role when valid data is provided")
    void findAllByUserRoles_ValidData_ShouldReturnListOfUserTelegram() {
        //Given
        UserTelegram existUserTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();
        List<UserTelegram> expectedUserRole = List.of(existUserTelegram);
        List<UserTelegram> expectedAdminRole = List.of();

        //When
        List<UserTelegram> actualUserRole = userTelegramRepository
                .findAllByUserRoles(Role.RoleName.USER);
        List<UserTelegram> actualAdminRole = userTelegramRepository
                .findAllByUserRoles(Role.RoleName.ADMIN);

        //Then
        assertEquals(expectedUserRole, actualUserRole,
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(expectedAdminRole, actualAdminRole,
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }
}
