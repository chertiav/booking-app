package com.chertiavdev.bookingapp.utils.helpers;

import java.sql.Connection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class RepositoriesTestUtils {
    private RepositoriesTestUtils() {
    }

    // ========================methods for generel usages===================================
    public static void executeSqlScripts(Connection connection, String... scripts) {
        for (String script : scripts) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(script));
        }
    }
}
