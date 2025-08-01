package com.chertiavdev.bookingapp.repository.role;

import static com.chertiavdev.bookingapp.model.Role.RoleName.ADMIN;
import static com.chertiavdev.bookingapp.model.Role.RoleName.USER;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.Role.RoleName;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("RoleRepository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @ParameterizedTest(name = "Case {index}: {0}")
    @MethodSource("validUsersRolesProvider")
    @DisplayName("Finding role by name successfully when valid data is provided")
    void findByName_ValidData_ShouldReturnRole(String caseName, RoleName roleName, Long roleId) {
        //When
        Optional<Role> actual = roleRepository.findByName(roleName);

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertEquals(roleName, actual.get().getName(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(roleId, actual.get().getId(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    private static Stream<Arguments> validUsersRolesProvider() {
        return Stream.of(
                arguments("Test ADMIN role", ADMIN, SAMPLE_TEST_ID_1),
                arguments("Test USER role", USER, SAMPLE_TEST_ID_2)
        );
    }
}
