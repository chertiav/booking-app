package com.chertiavdev.bookingapp.utils.helpers;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class RepositoriesTestUtils {
    private RepositoriesTestUtils() {
    }

    public static void executeSqlScripts(Connection connection, String... scripts) {
        for (String script : scripts) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(script));
        }
    }

    public static boolean recordExistsBeforeTimestamp(
            JdbcTemplate jdbcTemplate,
            String tableName,
            Instant now
    ) {
        return checkRecordExistence(jdbcTemplate, tableName, "expires_at < ?", Timestamp.from(now));
    }

    public static boolean recordExistsInDatabaseById(
            JdbcTemplate jdbcTemplate,
            String tableName,
            Long id
    ) {
        return checkRecordExistence(jdbcTemplate, tableName, "id = ?", id);
    }

    public static Integer countRecordsInDatabase(
            JdbcTemplate jdbcTemplate,
            String tableName,
            Instant now
    ) {
        String query = String.format(
                "SELECT COUNT(*) FROM %s WHERE expires_at < ? AND is_deleted = false",
                tableName);
        Timestamp timestamp = Timestamp.from(now);
        return jdbcTemplate.queryForObject(query, Integer.class, timestamp);
    }

    private static boolean checkRecordExistence(
            JdbcTemplate jdbcTemplate,
            String tableName,
            String condition,
            Object parameter
    ) {
        String query = String.format(
                "SELECT COUNT(*) FROM %s WHERE %s AND is_deleted = false",
                tableName,
                condition
        );
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, parameter);
        return count != null && count > 0;
    }
}
