package com.chertiavdev.bookingapp.repository.telegram.link;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TEST_TOKEN_INVALID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXPIRATION_TIMESTAMPS_ARE_DIFFERENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_BE_DELETED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_EXIST_BEFORE_DELETION;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.recordExistsBeforeTimestamp;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.isValidExpirationTime;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.UserTelegramLinkTestDataBuilder;
import com.chertiavdev.bookingapp.model.TelegramLink;
import java.sql.Connection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

@DisplayName("Telegram Link Repository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class TelegramLinkRepositoryTest {
    private static final String EXPIRATION_DATE_FIELD = "expiresAt";
    private static final String TELEGRAM_LINKS_TABLE_NAME = "telegram_links";
    private static final String[] SETUP_SCRIPTS = {
            "database/user/add-users-to-users-table.sql",
            "database/user/role/add-role-for-into-users_roles_table.sql",
            "database/telegram/add-telegram_link-to-telegram_links-table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/telegram/remove-all-telegram_links-from-telegram_links-table.sql",
            "database/user/role/remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
            "database/user/remove-users-where-id-more-than-one-from-users-table.sql"
    };
    @Autowired
    private TelegramLinkRepository telegramLinkRepository;
    @Autowired
    private UserTelegramLinkTestDataBuilder userTelegramLinkTestDataBuilder;
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
    @DisplayName("Find a Link by userId when valid data is provided should return "
            + "an Optional of TelegramLink")
    void findByUserId_ValidUserId_ShouldReturnOptionalOfTelegramLink() {
        //Given
        TelegramLink expected = userTelegramLinkTestDataBuilder.getTelegramLinkJohn();

        //When
        Optional<TelegramLink> actual = telegramLinkRepository
                .findByUserId(expected.getUser().getId());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertTrue(reflectionEquals(expected, actual.get(), EXPIRATION_DATE_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertTrue(
                isValidExpirationTime(
                        expected.getExpiresAt().truncatedTo(ChronoUnit.MINUTES),
                        actual.get().getExpiresAt().truncatedTo(ChronoUnit.MINUTES)),
                EXPIRATION_TIMESTAMPS_ARE_DIFFERENT);
    }

    @Test
    @DisplayName("Find a Link by userId when invalid userId is provided should return "
            + "an Optional of empty")
    void findByUserId_InValidUserId_ShouldReturnOptionalOfEmpty() {
        //When
        Optional<TelegramLink> actual = telegramLinkRepository
                .findByUserId(INVALID_TEST_ID);

        //Then
        assertFalse(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Find a Link by token when valid data is provided should return "
            + "an Optional of TelegramLink")
    void findByToken_ValidToken_ShouldReturnOptionalOfTelegramLink() {
        //Given
        TelegramLink expected = userTelegramLinkTestDataBuilder.getTelegramLinkJohn();

        //When
        Optional<TelegramLink> actual = telegramLinkRepository
                .findByToken(expected.getToken());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertTrue(reflectionEquals(expected, actual.get(), EXPIRATION_DATE_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertTrue(
                isValidExpirationTime(
                        expected.getExpiresAt().truncatedTo(ChronoUnit.MINUTES),
                        actual.get().getExpiresAt().truncatedTo(ChronoUnit.MINUTES)),
                EXPIRATION_TIMESTAMPS_ARE_DIFFERENT);
    }

    @Test
    @DisplayName("Find a Link by token when invalid token is provided should return "
            + "an Optional of empty")
    void findByUserId_InValidToken_ShouldReturnOptionalOfTelegramLink() {
        //When
        Optional<TelegramLink> actual = telegramLinkRepository
                .findByToken(TEST_TOKEN_INVALID);

        //Then
        assertFalse(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Delete a Link successfully when expiresAt is before the given timestamp")
    void deleteByExpiresAtBefore_ValidTimestamp_ShouldDeleteTelegramLink() {
        //Given
        TelegramLink telegramLink = userTelegramLinkTestDataBuilder.getExpiredTelegramLinkJohn();
        Instant now = Instant.now();
        boolean existsBefore = recordExistsBeforeTimestamp(
                jdbcTemplate,
                TELEGRAM_LINKS_TABLE_NAME,
                now
        );

        // When
        telegramLinkRepository.deleteByExpiresAtBefore(now);

        // Then
        Optional<TelegramLink> actual = telegramLinkRepository.findById(telegramLink.getId());

        assertTrue(existsBefore, RECORD_SHOULD_EXIST_BEFORE_DELETION);
        assertTrue(actual.isEmpty(), RECORD_SHOULD_BE_DELETED);
    }
}
