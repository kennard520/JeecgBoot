package org.jeecg.modules.custom.api.migration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomAiDm8MigrationContractTest {

    private static final String MIGRATION = "jeecg-boot/db/增量SQL/custom-ai-reliability-dm8.sql";

    @Test
    void migrationAddsOwnershipAndReliableDeliveryColumns() throws IOException {
        String sql = readMigration();

        assertTrue(sql.contains("TABLE_NAME = 'DOCUMENTS' AND COLUMN_NAME = 'CUSTOMER_CODE'"));
        assertTrue(sql.contains("TABLE_NAME = 'DOCUMENTS' AND COLUMN_NAME = 'UPLOADER_USER_ID'"));
        assertTrue(sql.contains("TABLE_NAME = 'DOCUMENTS' AND COLUMN_NAME = 'AGENT_CODE'"));
        assertTrue(sql.contains("TABLE_NAME = 'CUSTOM_CALLBACK_DELIVERY' AND COLUMN_NAME = 'CLAIM_TOKEN'"));
        assertTrue(sql.contains("TABLE_NAME = 'CUSTOM_CALLBACK_DELIVERY' AND COLUMN_NAME = 'CLAIMED_BY'"));
        assertTrue(sql.contains("COALESCE(\"LAST_HEARTBEAT_AT\", \"STARTED_AT\", \"CREATED_AT\", SYSDATE)"));
    }

    @Test
    void migrationBackfillsOnlyUnambiguousAppOwnership() throws IOException {
        String sql = readMigration();

        assertFalse(sql.contains("SELECT MIN(A.\"ID\")"));
        assertTrue(sql.contains("COUNT(*) FROM \"CUSTOM_API_APP\" A WHERE A.\"CUSTOMER_CODE\" = F.\"CUSTOMER_CODE\") = 1"));
        assertTrue(sql.contains("COUNT(*) FROM \"CUSTOM_API_APP\" A WHERE A.\"CUSTOMER_CODE\" = T.\"CUSTOMER_CODE\") = 1"));
    }

    @Test
    void migrationInstallsAdminMenusAndReconcilesLegacyRows() throws IOException {
        String sql = readMigration();

        assertTrue(sql.contains("custom/ai/grant/index"));
        assertTrue(sql.contains("custom/api/app/index"));
        assertTrue(sql.contains("SYS_ROLE_PERMISSION"));
        assertTrue(sql.contains("STATUS\" = 'timeout'"));
        assertTrue(sql.contains("STATUS\" = 'FAILED'"));
    }

    private static String readMigration() throws IOException {
        Path current = Paths.get("").toAbsolutePath();
        while (current != null) {
            Path candidate = current.resolve(MIGRATION);
            if (Files.isRegularFile(candidate)) {
                return Files.readString(candidate, StandardCharsets.UTF_8);
            }
            current = current.getParent();
        }
        throw new IOException("Cannot locate " + MIGRATION);
    }
}
