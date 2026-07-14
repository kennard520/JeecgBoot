package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.service.impl.CustomApiFileServiceImpl;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.jeecg.modules.custom.api.util.CanonicalRequestHasher;
import org.jeecg.modules.custom.api.validation.UploadedFileVerifier;
import org.jeecg.modules.custom.api.validation.VerifiedFile;
import org.jeecg.modules.custom.api.vo.FileCompleteRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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
}
