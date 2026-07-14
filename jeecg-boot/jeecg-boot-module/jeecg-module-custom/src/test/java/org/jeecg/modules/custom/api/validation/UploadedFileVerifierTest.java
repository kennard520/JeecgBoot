package org.jeecg.modules.custom.api.validation;

import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UploadedFileVerifierTest {

    private final ObjectStorageService storage = mock(ObjectStorageService.class);

    @Test
    void rejectsMissingObject() throws Exception {
        CustomApiFile file = expectedZip(new byte[]{1});
        when(storage.openStream(file)).thenThrow(new JeecgBootException("object not found"));

        assertThatThrownBy(() -> verifier(10).verify(file))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("object not found");
    }

    @Test
    void rejectsEmptyObject() {
        CustomApiFile file = expectedZip(new byte[0]);
        supply(file, new byte[0]);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("empty");
    }

    @Test
    void rejectsActualSizeMismatch() throws Exception {
        byte[] bytes = zip(Map.of("invoice.txt", "ok"));
        CustomApiFile file = expectedZip(bytes).setFileSize((long) bytes.length + 1);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("size");
    }

    @Test
    void rejectsActualSha256Mismatch() throws Exception {
        byte[] bytes = zip(Map.of("invoice.txt", "ok"));
        CustomApiFile file = expectedZip(bytes).setSha256("0".repeat(64));
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("SHA256");
    }

    @Test
    void rejectsNonZipMagic() {
        byte[] bytes = "not-a-zip".getBytes();
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("magic");
    }

    @Test
    void rejectsPathTraversalEntry() throws Exception {
        byte[] bytes = zip(Map.of("../secret.txt", "bad"));
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("path");
    }

    @Test
    void rejectsEncryptedEntryFlag() throws Exception {
        byte[] bytes = markEncrypted(zip(Map.of("invoice.txt", "secret")));
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("encrypted");
    }

    @Test
    void rejectsNestedZipArchive() throws Exception {
        byte[] nested = zip(Map.of("payload.txt", "nested"));
        byte[] bytes = zipBytes(Map.of("nested.zip", nested));
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("nested ZIP");
    }

    @Test
    void rejectsNestedZipArchiveEvenWhenExtensionIsDisguised() throws Exception {
        byte[] nested = zip(Map.of("payload.txt", "nested"));
        byte[] bytes = zipBytes(Map.of("payload.bin", nested));
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("nested ZIP");
    }

    @Test
    void allowsAValidXlsxPackageInsideTheUploadedCaseZip() throws Exception {
        byte[] xlsx = zip(Map.of(
                "[Content_Types].xml", "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"/>",
                "_rels/.rels", "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"/>",
                "xl/workbook.xml", "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"/>"
        ));
        byte[] bytes = zipBytes(Map.of("documents/invoice.xlsx", xlsx));
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        VerifiedFile verified = verifier(20).verify(file);

        assertThat(verified.detectedType()).isEqualTo("application/zip");
    }

    @Test
    void rejectsUnixSymbolicLinkEntry() throws Exception {
        byte[] bytes = symlinkZip();
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10).verify(file))
                .hasMessageContaining("symbolic links");
    }

    @Test
    void rejectsZipBombByCompressionRatio() throws Exception {
        byte[] payload = new byte[64 * 1024];
        byte[] bytes = zipBytes(Map.of("payload.bin", payload));
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(10, 2L).verify(file))
                .hasMessageContaining("compression ratio");
    }

    @Test
    void rejectsTooManyEntries() throws Exception {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("one.txt", "1");
        entries.put("two.txt", "2");
        byte[] bytes = zip(entries);
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        assertThatThrownBy(() -> verifier(1).verify(file))
                .hasMessageContaining("entries");
    }

    @Test
    void verifiesValidZipAndReturnsTrustedMetadata() throws Exception {
        byte[] bytes = zip(Map.of("docs/invoice.txt", "invoice-data"));
        CustomApiFile file = expectedZip(bytes);
        supply(file, bytes);

        VerifiedFile verified = verifier(10).verify(file);

        assertThat(verified.actualFileSize()).isEqualTo(bytes.length);
        assertThat(verified.actualSha256()).isEqualTo(sha256(bytes));
        assertThat(verified.detectedType()).isEqualTo("application/zip");
    }

    private UploadedFileVerifier verifier(int maxEntries) {
        return verifier(maxEntries, 100L);
    }

    private UploadedFileVerifier verifier(int maxEntries, long maxCompressionRatio) {
        return new UploadedFileVerifier(storage, 2 * 1024 * 1024L, maxEntries,
                1024 * 1024L, 2 * 1024 * 1024L, maxCompressionRatio);
    }

    private CustomApiFile expectedZip(byte[] bytes) {
        return new CustomApiFile()
                .setFileId("file-1")
                .setOriginalFilename("case.zip")
                .setContentType("application/zip")
                .setFileSize((long) bytes.length)
                .setSha256(sha256(bytes));
    }

    private void supply(CustomApiFile file, byte[] bytes) {
        try {
            when(storage.openStream(file)).thenAnswer(ignored -> new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private byte[] zip(Map<String, String> entries) throws IOException {
        Map<String, byte[]> binaryEntries = new LinkedHashMap<>();
        entries.forEach((name, value) -> binaryEntries.put(name, value.getBytes(StandardCharsets.UTF_8)));
        return zipBytes(binaryEntries);
    }

    private byte[] zipBytes(Map<String, byte[]> entries) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue());
                zip.closeEntry();
            }
        }
        return out.toByteArray();
    }

    private byte[] symlinkZip() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out)) {
            ZipArchiveEntry link = new ZipArchiveEntry("invoice-link");
            link.setUnixMode(UnixStat.LINK_FLAG | 0777);
            zip.putArchiveEntry(link);
            zip.write("invoice.txt".getBytes(StandardCharsets.UTF_8));
            zip.closeArchiveEntry();
        }
        return out.toByteArray();
    }

    private byte[] markEncrypted(byte[] bytes) {
        for (int i = 0; i + 10 < bytes.length; i++) {
            boolean local = bytes[i] == 0x50 && bytes[i + 1] == 0x4b && bytes[i + 2] == 0x03 && bytes[i + 3] == 0x04;
            boolean central = bytes[i] == 0x50 && bytes[i + 1] == 0x4b && bytes[i + 2] == 0x01 && bytes[i + 3] == 0x02;
            if (local) {
                bytes[i + 6] = (byte) (bytes[i + 6] | 0x01);
            }
            if (central) {
                bytes[i + 8] = (byte) (bytes[i + 8] | 0x01);
            }
        }
        return bytes;
    }

    private String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
