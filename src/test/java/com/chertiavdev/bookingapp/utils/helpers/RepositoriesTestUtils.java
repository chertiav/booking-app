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

    public static boolean recordExistsInDatabase(
            JdbcTemplate jdbcTemplate,
            String tableName,
            Instant now
    ) {
        String query = String.format(
                "SELECT COUNT(*) FROM %s WHERE expires_at < ? AND is_deleted = false",
                tableName);
        Timestamp timestamp = Timestamp.from(now);
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, timestamp);
        return count != null && count > 0;
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
}
