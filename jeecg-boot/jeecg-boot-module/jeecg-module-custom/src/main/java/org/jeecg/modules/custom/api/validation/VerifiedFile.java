package org.jeecg.modules.custom.api.validation;

public record VerifiedFile(long actualFileSize, String actualSha256, String detectedType) {
}
