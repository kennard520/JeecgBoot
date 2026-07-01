package org.jeecg.modules.custom.api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class CustomApiIds {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private CustomApiIds() {
    }

    public static String fileId() {
        return "file_" + LocalDateTime.now().format(FORMATTER) + "_" + CustomApiCrypto.randomToken("", 4);
    }

    public static String taskId() {
        return "task_" + LocalDateTime.now().format(FORMATTER) + "_" + CustomApiCrypto.randomToken("", 4);
    }

    public static String safeFilename(String filename) {
        String value = filename == null || filename.isBlank() ? "upload.zip" : filename;
        value = value.replace("\\", "/");
        int index = value.lastIndexOf('/');
        if (index >= 0) {
            value = value.substring(index + 1);
        }
        return value.replaceAll("[^A-Za-z0-9._()-]", "_");
    }
}
