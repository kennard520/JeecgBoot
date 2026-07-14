package org.jeecg.modules.custom.api.controller;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomApiInternalControllerTest {

    @Test
    void streamsVerifiedFileForActiveOwnedTask() throws Exception {
        Fixture fixture = fixture("CUSTOMER-A", "CUSTOMER-A");
        when(fixture.storage.openStream(fixture.file))
                .thenReturn(new ByteArrayInputStream("zip".getBytes()));

        ResponseEntity<InputStreamResource> response = fixture.controller.downloadTaskFile(
                "task-1", "file-1", "internal-secret");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInputStream().readAllBytes()).isEqualTo("zip".getBytes());
    }

    @Test
    void rejectsCrossCustomerFileBeforeOpeningStorage() throws Exception {
        Fixture fixture = fixture("CUSTOMER-A", "CUSTOMER-B");

        assertThatThrownBy(() -> fixture.controller.downloadTaskFile(
                "task-1", "file-1", "internal-secret"))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("not available");
        verify(fixture.storage, never()).openStream(any());
    }

    private Fixture fixture(String taskCustomer, String fileCustomer) throws Exception {
        CustomApiInternalController controller = new CustomApiInternalController();
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        ICustomApiFileService fileService = mock(ICustomApiFileService.class);
        ObjectStorageService storage = mock(ObjectStorageService.class);
        setField(controller, "taskMapper", taskMapper);
        setField(controller, "fileService", fileService);
        setField(controller, "objectStorageService", storage);
        setField(controller, "internalToken", "internal-secret");
        CustomApiTask task = new CustomApiTask().setTaskId("task-1").setFileId("file-1")
                .setCustomerCode(taskCustomer).setStatus(CustomApiTask.STATUS_QUEUED);
        CustomApiFile file = new CustomApiFile().setFileId("file-1").setCustomerCode(fileCustomer)
                .setOriginalFilename("case.zip").setContentType("application/zip")
                .setActualFileSize(3L).setStatus(CustomApiFile.STATUS_UPLOADED);
        when(taskMapper.selectOne(any())).thenReturn(task);
        when(fileService.getOne(any(), eq(false))).thenReturn(file);
        return new Fixture(controller, storage, file);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private record Fixture(CustomApiInternalController controller,
                           ObjectStorageService storage,
                           CustomApiFile file) {
    }
}
