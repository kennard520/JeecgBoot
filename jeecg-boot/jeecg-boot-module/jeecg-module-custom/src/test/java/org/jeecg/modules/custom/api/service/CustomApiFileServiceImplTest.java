package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.service.impl.CustomApiFileServiceImpl;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.jeecg.modules.custom.api.util.CanonicalRequestHasher;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
import org.jeecg.modules.custom.api.validation.UploadedFileVerifier;
import org.jeecg.modules.custom.api.validation.VerifiedFile;
import org.jeecg.modules.custom.api.vo.FileCompleteRequest;
import org.jeecg.modules.custom.api.vo.FileUploadUrlRequest;
import org.jeecg.modules.custom.api.vo.FileUploadUrlResponse;
import org.jeecg.common.exception.JeecgBootException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CustomApiFileServiceImplTest {

    @Test
    void completePersistsServerVerifiedValuesInsteadOfClientClaims() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        UploadedFileVerifier verifier = mock(UploadedFileVerifier.class);
        CustomApiIdempotencyService idempotency = mock(CustomApiIdempotencyService.class);
        CustomApiFileServiceImpl service = spy(new CustomApiFileServiceImpl(
                storage, verifier, idempotency, new CanonicalRequestHasher()
        ));
        CustomApiApp app = new CustomApiApp().setId(9L).setCustomerCode("CUSTOMER-A");
        CustomApiFile file = new CustomApiFile()
                .setId(1L)
                .setAppId(9L)
                .setCustomerCode("CUSTOMER-A")
                .setFileId("file-1")
                .setStatus(CustomApiFile.STATUS_PENDING)
                .setExpiresAt(LocalDateTime.now().plusMinutes(5));
        doReturn(file).when(service).getOne(any(), eq(false));
        doReturn(true).when(service).updateById(any());
        when(verifier.verify(file)).thenReturn(new VerifiedFile(321L, "a".repeat(64), "application/zip"));

        FileCompleteRequest request = new FileCompleteRequest();
        request.setFileSize(999L);
        request.setSha256("b".repeat(64));
        service.complete(app, "file-1", request);

        assertThat(file.getActualFileSize()).isEqualTo(321L);
        assertThat(file.getActualSha256()).isEqualTo("a".repeat(64));
        assertThat(file.getFileSize()).isEqualTo(321L);
        assertThat(file.getSha256()).isEqualTo("a".repeat(64));
        assertThat(file.getVerifiedAt()).isNotNull();
        assertThat(file.getStatus()).isEqualTo(CustomApiFile.STATUS_UPLOADED);
        verify(service).updateById(file);
    }

    @Test
    void completeRejectsAnAlreadyUploadedFile() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        UploadedFileVerifier verifier = mock(UploadedFileVerifier.class);
        CustomApiIdempotencyService idempotency = mock(CustomApiIdempotencyService.class);
        CustomApiFileServiceImpl service = spy(new CustomApiFileServiceImpl(
                storage, verifier, idempotency, new CanonicalRequestHasher()
        ));
        CustomApiApp app = new CustomApiApp().setId(9L).setCustomerCode("CUSTOMER-A");
        CustomApiFile file = ownedFile(CustomApiFile.STATUS_UPLOADED)
                .setExpiresAt(LocalDateTime.now().plusMinutes(5));
        doReturn(file).when(service).getOne(any(), eq(false));
        when(verifier.verify(file)).thenReturn(new VerifiedFile(321L, "a".repeat(64), "application/zip"));

        assertThatThrownBy(() -> service.complete(app, "file-1", new FileCompleteRequest()))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("status");

        verifyNoInteractions(verifier, storage);
    }

    @Test
    void completeSwitchesFromWritableStagingKeyToImmutableObjectKey() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        UploadedFileVerifier verifier = mock(UploadedFileVerifier.class);
        CustomApiIdempotencyService idempotency = mock(CustomApiIdempotencyService.class);
        CustomApiFileServiceImpl service = spy(new CustomApiFileServiceImpl(
                storage, verifier, idempotency, new CanonicalRequestHasher()
        ));
        CustomApiApp app = new CustomApiApp().setId(9L).setCustomerCode("CUSTOMER-A");
        String stagingKey = "custom-api/uploads/2026/07/14/file-1/case.zip";
        CustomApiFile file = ownedFile(CustomApiFile.STATUS_PENDING)
                .setOriginalFilename("case.zip")
                .setObjectKey(stagingKey)
                .setExpiresAt(LocalDateTime.now().plusMinutes(5));
        doReturn(file).when(service).getOne(any(), eq(false));
        doReturn(true).when(service).updateById(any());
        when(verifier.verify(file)).thenReturn(new VerifiedFile(321L, "a".repeat(64), "application/zip"));

        service.complete(app, "file-1", new FileCompleteRequest());

        assertThat(file.getObjectKey())
                .isNotEqualTo(stagingKey)
                .startsWith("custom-api/objects/file-1/");
        assertThat(file.getUploadTokenHash()).isNull();
    }

    @Test
    void completeFreezesSnapshotBeforeVerifierReadsTheObject() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        UploadedFileVerifier verifier = mock(UploadedFileVerifier.class);
        CustomApiIdempotencyService idempotency = mock(CustomApiIdempotencyService.class);
        CustomApiFileServiceImpl service = spy(new CustomApiFileServiceImpl(
                storage, verifier, idempotency, new CanonicalRequestHasher()
        ));
        CustomApiApp app = new CustomApiApp().setId(9L).setCustomerCode("CUSTOMER-A");
        CustomApiFile file = ownedFile(CustomApiFile.STATUS_PENDING)
                .setOriginalFilename("case.zip")
                .setObjectKey("custom-api/uploads/file-1/case.zip")
                .setExpiresAt(LocalDateTime.now().plusMinutes(5));
        doReturn(file).when(service).getOne(any(), eq(false));
        doReturn(true).when(service).updateById(any());
        when(verifier.verify(file)).thenReturn(new VerifiedFile(321L, "a".repeat(64), "application/zip"));

        service.complete(app, "file-1", new FileCompleteRequest());

        InOrder order = inOrder(storage, verifier);
        order.verify(storage).freezeUploadedObject(eq(file), anyString());
        order.verify(verifier).verify(file);
    }

    @Test
    void localUploadRejectsCompletedFileEvenWhenTokenMatches() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        CustomApiFileServiceImpl service = fileService(storage);
        String uploadToken = "upl_once";
        CustomApiFile file = ownedFile(CustomApiFile.STATUS_UPLOADED)
                .setUploadTokenHash(CustomApiCrypto.sha256(uploadToken))
                .setExpiresAt(LocalDateTime.now().plusMinutes(5));
        doReturn(file).when(service).getOne(any(), eq(false));
        MockMultipartFile upload = new MockMultipartFile("file", "case.zip", "application/zip", new byte[]{1});

        assertThatThrownBy(() -> service.uploadLocalContent("file-1", uploadToken, upload))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("status");

        verifyNoInteractions(storage);
    }

    @Test
    void localUploadTokenIsConsumedAfterOneSuccessfulPendingUpload() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        CustomApiFileServiceImpl service = fileService(storage);
        String uploadToken = "upl_once";
        CustomApiFile file = ownedFile(CustomApiFile.STATUS_PENDING)
                .setUploadTokenHash(CustomApiCrypto.sha256(uploadToken))
                .setExpiresAt(LocalDateTime.now().plusMinutes(5));
        doReturn(file).when(service).getOne(any(), eq(false));
        doReturn(true).when(service).updateById(any());
        MockMultipartFile upload = new MockMultipartFile("file", "case.zip", "application/zip", new byte[]{1});

        service.uploadLocalContent("file-1", uploadToken, upload);

        assertThatThrownBy(() -> service.uploadLocalContent("file-1", uploadToken, upload))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("token");
        assertThat(file.getUploadTokenHash()).isNull();
        verify(storage, times(1)).saveLocalUpload(file, upload);
    }

    @Test
    void idempotentRetryReusesTheSameUnexpiredUploadCapability() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        UploadedFileVerifier verifier = mock(UploadedFileVerifier.class);
        CustomApiIdempotencyService idempotency = mock(CustomApiIdempotencyService.class);
        CustomApiFileServiceImpl service = spy(new CustomApiFileServiceImpl(
                storage, verifier, idempotency, new CanonicalRequestHasher()
        ));
        ReflectionTestUtils.setField(service, "uploadCapabilitySecret", "test-upload-capability-secret");
        CustomApiApp app = new CustomApiApp().setId(9L).setCustomerCode("CUSTOMER-A");
        FileUploadUrlRequest request = new FileUploadUrlRequest();
        request.setFilename("case.zip");
        request.setContentType("application/zip");
        request.setFileSize(123L);
        request.setClientFileId("client-file-1");
        request.setIdempotencyKey("idem-file-1");

        AtomicReference<CustomApiFile> inserted = new AtomicReference<>();
        List<String> issuedTokens = new ArrayList<>();
        when(idempotency.findFile(eq(9L), eq("client-file-1"), eq("idem-file-1"), anyString()))
                .thenAnswer(ignored -> inserted.get());
        when(storage.createUploadUrl(any(), anyString(), any())).thenAnswer(invocation -> {
            CustomApiFile file = invocation.getArgument(0);
            String token = invocation.getArgument(1);
            issuedTokens.add(token);
            return new FileUploadUrlResponse()
                    .setFileId(file.getFileId())
                    .setObjectKey(file.getObjectKey())
                    .setUploadUrl(token)
                    .setExpiresAt(file.getExpiresAt());
        });
        when(idempotency.insertFileOrFindWinner(any(CustomApiFile.class))).thenAnswer(invocation -> {
            CustomApiFile candidate = invocation.getArgument(0);
            inserted.set(candidate);
            return candidate;
        });
        doReturn(true).when(service).updateById(any());

        FileUploadUrlResponse first = service.createUploadUrl(app, request, mock(jakarta.servlet.http.HttpServletRequest.class));
        LocalDateTime generatedExpiry = inserted.get().getExpiresAt();
        int persistedNano = generatedExpiry.getNano() == 0 ? 999_000_000 : 0;
        inserted.get().setExpiresAt(generatedExpiry.withNano(persistedNano));
        FileUploadUrlResponse retry = service.createUploadUrl(app, request, mock(jakarta.servlet.http.HttpServletRequest.class));

        assertThat(issuedTokens).hasSize(2);
        assertThat(issuedTokens.get(1)).isEqualTo(issuedTokens.get(0));
        assertThat(retry.getExpiresAt().withNano(0)).isEqualTo(first.getExpiresAt().withNano(0));
        verify(service, never()).updateById(any());
    }

    @Test
    void concurrentInsertRaceReturnsThePersistedWinner() {
        ObjectStorageService storage = mock(ObjectStorageService.class);
        UploadedFileVerifier verifier = mock(UploadedFileVerifier.class);
        CustomApiIdempotencyService idempotency = mock(CustomApiIdempotencyService.class);
        CustomApiFileServiceImpl service = spy(new CustomApiFileServiceImpl(
                storage, verifier, idempotency, new CanonicalRequestHasher()
        ));
        ReflectionTestUtils.setField(service, "uploadCapabilitySecret", "test-upload-capability-secret");
        CustomApiApp app = new CustomApiApp().setId(9L).setCustomerCode("CUSTOMER-A");
        FileUploadUrlRequest request = new FileUploadUrlRequest();
        request.setFilename("case.zip");
        request.setContentType("application/zip");
        request.setFileSize(123L);
        request.setClientFileId("client-file-1");
        request.setIdempotencyKey("idem-file-1");
        CustomApiFile winner = ownedFile(CustomApiFile.STATUS_UPLOADED)
                .setFileId("file-winner")
                .setObjectKey("custom-api/objects/file-winner/version-1/case.zip");
        when(idempotency.findFile(eq(9L), eq("client-file-1"), eq("idem-file-1"), anyString()))
                .thenReturn(null);
        when(storage.createUploadUrl(any(), anyString(), any())).thenAnswer(invocation -> {
            CustomApiFile candidate = invocation.getArgument(0);
            return new FileUploadUrlResponse().setFileId(candidate.getFileId());
        });
        when(idempotency.insertFileOrFindWinner(any(CustomApiFile.class))).thenReturn(winner);

        FileUploadUrlResponse response = service.createUploadUrl(
                app, request, mock(jakarta.servlet.http.HttpServletRequest.class));

        assertThat(response.getFileId()).isEqualTo("file-winner");
        assertThat(response.getObjectKey()).isEqualTo(winner.getObjectKey());
    }

    private CustomApiFileServiceImpl fileService(ObjectStorageService storage) {
        return spy(new CustomApiFileServiceImpl(
                storage,
                mock(UploadedFileVerifier.class),
                mock(CustomApiIdempotencyService.class),
                new CanonicalRequestHasher()
        ));
    }

    private CustomApiFile ownedFile(String status) {
        return new CustomApiFile()
                .setId(1L)
                .setAppId(9L)
                .setCustomerCode("CUSTOMER-A")
                .setFileId("file-1")
                .setStatus(status);
    }
}
