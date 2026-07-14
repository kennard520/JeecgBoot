package org.jeecg.modules.custom.api.storage;

import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultObjectStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void localUploadCapabilityIsSentInAHeaderInsteadOfTheUrl() {
        DefaultObjectStorageService storage = new DefaultObjectStorageService();
        ReflectionTestUtils.setField(storage, "uploadType", CommonConstant.UPLOAD_TYPE_LOCAL);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("smart-entry.citclub.org");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("");
        CustomApiFile file = new CustomApiFile()
                .setFileId("file-1")
                .setContentType("application/zip")
                .setExpiresAt(LocalDateTime.now().plusMinutes(5))
                .setObjectKey("custom-api/uploads/file-1/case.zip");

        var response = storage.createUploadUrl(file, "secret-capability", request);

        assertThat(response.getUploadUrl()).doesNotContain("secret-capability").doesNotContain("uploadToken=");
        assertThat(response.getHeaders()).containsEntry("X-Custom-Upload-Token", "secret-capability");
    }

    @Test
    void externalApiDefaultsToLocalWhenGlobalCosHasNoCredentials() {
        DefaultObjectStorageService storage = new DefaultObjectStorageService();
        ReflectionTestUtils.setField(storage, "uploadType", CommonConstant.UPLOAD_TYPE_TENCENT_COS);
        ReflectionTestUtils.setField(storage, "cosSecretId", "");
        ReflectionTestUtils.setField(storage, "cosSecretKey", "");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("smart-entry.citclub.org");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("");
        CustomApiFile file = new CustomApiFile()
                .setFileId("file-1")
                .setContentType("application/zip")
                .setExpiresAt(LocalDateTime.now().plusMinutes(5))
                .setObjectKey("custom-api/uploads/file-1/case.zip");

        var response = storage.createUploadUrl(file, "secret-capability", request);

        assertThat(response.getStorageType()).isEqualTo(CommonConstant.UPLOAD_TYPE_LOCAL);
        assertThat(response.getUploadMethod()).isEqualTo("POST");
        assertThat(response.getHeaders()).containsEntry("X-Custom-Upload-Token", "secret-capability");
    }

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

    @Test
    void deletingALocalObjectRemovesOnlyTheBoundFile() throws Exception {
        DefaultObjectStorageService storage = new DefaultObjectStorageService();
        ReflectionTestUtils.setField(storage, "uploadPath", tempDir.toString());
        Path target = tempDir.resolve("custom-api/objects/file-1/version-1/case.zip");
        Files.createDirectories(target.getParent());
        Files.write(target, new byte[]{1, 2, 3});
        CustomApiFile file = new CustomApiFile()
                .setStorageType(CommonConstant.UPLOAD_TYPE_LOCAL)
                .setStoragePath(target.toString())
                .setObjectKey("custom-api/objects/file-1/version-1/case.zip");

        storage.deleteObject(file);

        assertThat(target).doesNotExist();
        assertThat(tempDir).exists();
    }
}
