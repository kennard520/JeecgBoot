package org.jeecg.modules.custom.task;

import org.jeecg.modules.custom.task.controller.DocumentController;
import org.jeecg.modules.custom.task.entity.Document;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentOwnershipContractTest {

    @Test
    void documentPersistsCustomerUploaderAndSelectedAgent() throws Exception {
        assertThat(Document.class.getDeclaredField("customerCode")).isNotNull();
        assertThat(Document.class.getDeclaredField("uploaderUserId")).isNotNull();
        assertThat(Document.class.getDeclaredField("agentCode")).isNotNull();
    }

    @Test
    void uploadContractCarriesAgentAndAutoStartToTheService() throws Exception {
        assertThat(DocumentController.class.getMethod(
                "uploadZip", MultipartFile.class, String.class, boolean.class)).isNotNull();
        assertThat(IDocumentService.class.getMethod(
                "uploadZip", MultipartFile.class, String.class, boolean.class)).isNotNull();
    }

    @Test
    void webUploadUsesTheSameHardenedVerifierAsExternalApiUploads() throws IOException {
        String source = Files.readString(locateDocumentService(), StandardCharsets.UTF_8);

        assertThat(source).contains("uploadedFileVerifier.verify(file)");
    }

    private Path locateDocumentService() throws IOException {
        String relative = "jeecg-boot/jeecg-boot-module/jeecg-module-custom/src/main/java/"
                + "org/jeecg/modules/custom/task/service/impl/DocumentServiceImpl.java";
        Path current = Paths.get("").toAbsolutePath();
        while (current != null) {
            Path candidate = current.resolve(relative);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        throw new IOException("Cannot locate DocumentServiceImpl.java");
    }
}
