package com.chertiavdev.bookingapp.repository.user;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_JOHN;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_INVALID_EMAIL_EXAMPLE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.model.User;
import java.sql.Connection;
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

@DisplayName("User Repository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTestDataBuilder userTestDataBuilder;

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
                    "database/user/add-users-to-users-table.sql",
                    "database/user/role/add-role-for-into-users_roles_table.sql"
            );
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection,
                    "database/user/role/"
                            + "remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
                    "database/user/remove-users-where-id-more-than-one-from-users-table.sql"
            );
        }
    }

    @Test
    @DisplayName("Find user by email when valid email is provided should return true")
    void existsByEmail_ValidEmail_ShouldReturnTrue() {
        //Where
        boolean actual = userRepository.existsByEmail(USER_EMAIL_JOHN);

        //Then
        assertTrue(actual, ACTUAL_RESULT_SHOULD_BE_PRESENT);
    }

    @Test
    @DisplayName("Find user by email when invalid email is provided should return false")
    void existsByEmail_InValidEmail_ShouldReturnFalse() {
        //Where
        boolean actual = userRepository.existsByEmail(USER_INVALID_EMAIL_EXAMPLE);

        //Then
        assertFalse(actual, ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Find user by email when valid email is provided should return user")
    void findByEmail_ValidEmail_ShouldReturnUser() {
        //Given
        User expected = userTestDataBuilder.getUserJohn();

        //Where
        Optional<User> actual = userRepository.findByEmail(expected.getEmail());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find user by email when Invalid email is provided should return optional empty")
    void findByEmail_InValidEmail_ShouldReturnOptionalEmpty() {
        //Where
        Optional<User> actual = userRepository.findByEmail(USER_INVALID_EMAIL_EXAMPLE);

        //Then
        assertFalse(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }
}
