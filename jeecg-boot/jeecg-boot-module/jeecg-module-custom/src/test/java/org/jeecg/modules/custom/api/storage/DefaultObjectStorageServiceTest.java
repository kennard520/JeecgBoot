package org.jeecg.modules.custom.api.storage;

import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultObjectStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void localFreezeCopiesStagingContentToNewImmutableObject() throws Exception {
        DefaultObjectStorageService storage = new DefaultObjectStorageService();
        ReflectionTestUtils.setField(storage, "uploadPath", tempDir.toString());
        Path staging = tempDir.resolve("staging/case.zip");
        Files.createDirectories(staging.getParent());
        Files.write(staging, new byte[]{1, 2, 3});
        CustomApiFile file = new CustomApiFile()
                .setFileId("file-1")
                .setOriginalFilename("case.zip")
                .setStorageType(CommonConstant.UPLOAD_TYPE_LOCAL)
                .setStoragePath(staging.toString())
                .setObjectKey("custom-api/uploads/file-1/case.zip");
        String immutableKey = "custom-api/objects/file-1/version-1/case.zip";

        storage.freezeUploadedObject(file, immutableKey);
        Path frozen = Path.of(file.getStoragePath());

        assertThat(file.getObjectKey()).isEqualTo(immutableKey);
        assertThat(frozen).isNotEqualTo(staging).exists();
        assertThat(Files.readAllBytes(frozen)).containsExactly(1, 2, 3);

        Files.write(staging, new byte[]{9, 9, 9});
        assertThat(Files.readAllBytes(frozen)).containsExactly(1, 2, 3);
    }

    @Test
    void localFreezeNeverOverwritesAnExistingImmutableObject() throws Exception {
        DefaultObjectStorageService storage = new DefaultObjectStorageService();
        ReflectionTestUtils.setField(storage, "uploadPath", tempDir.toString());
        Path staging = tempDir.resolve("staging/case.zip");
        Files.createDirectories(staging.getParent());
        Files.write(staging, new byte[]{1, 2, 3});
        String immutableKey = "custom-api/objects/file-1/version-1/case.zip";
        Path frozen = tempDir.resolve(immutableKey);
        Files.createDirectories(frozen.getParent());
        Files.write(frozen, new byte[]{7, 8, 9});
        CustomApiFile file = new CustomApiFile()
                .setFileId("file-1")
                .setOriginalFilename("case.zip")
                .setStorageType(CommonConstant.UPLOAD_TYPE_LOCAL)
                .setStoragePath(staging.toString())
                .setObjectKey("custom-api/uploads/file-1/case.zip");

        assertThatThrownBy(() -> storage.freezeUploadedObject(file, immutableKey))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("already exists");
        assertThat(Files.readAllBytes(frozen)).containsExactly(7, 8, 9);
    }
}
