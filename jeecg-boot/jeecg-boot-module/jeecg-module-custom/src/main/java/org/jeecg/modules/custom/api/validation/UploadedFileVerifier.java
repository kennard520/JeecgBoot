package org.jeecg.modules.custom.api.validation;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.Locale;
import java.util.zip.CRC32;

@Component
public class UploadedFileVerifier {
    private final ObjectStorageService storageService;
    private final long maxUploadBytes;
    private final int maxZipEntries;
    private final long maxZipEntryBytes;
    private final long maxZipTotalBytes;
    private final long maxCompressionRatio;

    public UploadedFileVerifier(ObjectStorageService storageService,
                                @Value("${custom.api.file.max-upload-bytes:104857600}") long maxUploadBytes,
                                @Value("${custom.api.file.max-zip-entries:2000}") int maxZipEntries,
                                @Value("${custom.api.file.max-zip-entry-bytes:209715200}") long maxZipEntryBytes,
                                @Value("${custom.api.file.max-zip-total-bytes:1073741824}") long maxZipTotalBytes,
                                @Value("${custom.api.file.max-compression-ratio:100}") long maxCompressionRatio) {
        this.storageService = storageService;
        this.maxUploadBytes = maxUploadBytes;
        this.maxZipEntries = maxZipEntries;
        this.maxZipEntryBytes = maxZipEntryBytes;
        this.maxZipTotalBytes = maxZipTotalBytes;
        this.maxCompressionRatio = maxCompressionRatio;
    }

    public VerifiedFile verify(CustomApiFile file) {
        if (file == null) {
            throw new JeecgBootException("file is required");
        }
        Path temp = null;
        try {
            temp = Files.createTempFile("custom-api-verify-", ".bin");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long actualSize = copyAndDigest(file, temp, digest);
            if (actualSize == 0) {
                throw new JeecgBootException("uploaded file is empty");
            }
            if (file.getFileSize() != null && file.getFileSize() != actualSize) {
                throw new JeecgBootException("uploaded file size mismatch");
            }
            String actualSha = HexFormat.of().formatHex(digest.digest());
            if (!blank(file.getSha256()) && !actualSha.equalsIgnoreCase(file.getSha256().trim())) {
                throw new JeecgBootException("uploaded file SHA256 mismatch");
            }
            String detectedType = detectType(temp);
            validateDeclaredType(file, detectedType);
            if ("application/zip".equals(detectedType)) {
                verifyZip(temp);
            }
            return new VerifiedFile(actualSize, actualSha, detectedType);
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new JeecgBootException("uploaded file verification failed: " + e.getMessage());
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private long copyAndDigest(CustomApiFile file, Path target, MessageDigest digest) throws IOException {
        long total = 0L;
        byte[] buffer = new byte[64 * 1024];
        try (InputStream input = storageService.openStream(file);
             OutputStream output = Files.newOutputStream(target)) {
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > maxUploadBytes) {
                    throw new JeecgBootException("uploaded file exceeds maximum size");
                }
                digest.update(buffer, 0, read);
                output.write(buffer, 0, read);
            }
        }
        return total;
    }

    private String detectType(Path file) throws IOException {
        byte[] magic = new byte[8];
        int read;
        try (InputStream input = Files.newInputStream(file)) {
            read = input.read(magic);
        }
        if (read >= 4 && magic[0] == 0x50 && magic[1] == 0x4b
                && ((magic[2] == 0x03 && magic[3] == 0x04)
                || (magic[2] == 0x05 && magic[3] == 0x06)
                || (magic[2] == 0x07 && magic[3] == 0x08))) {
            return "application/zip";
        }
        if (read >= 5 && magic[0] == '%' && magic[1] == 'P' && magic[2] == 'D' && magic[3] == 'F' && magic[4] == '-') {
            return "application/pdf";
        }
        if (read >= 7 && magic[0] == 'R' && magic[1] == 'a' && magic[2] == 'r' && magic[3] == '!'
                && magic[4] == 0x1a && magic[5] == 0x07) {
            return "application/vnd.rar";
        }
        throw new JeecgBootException("file magic does not match a supported type");
    }

    private void validateDeclaredType(CustomApiFile file, String detectedType) {
        String name = blank(file.getOriginalFilename()) ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        boolean extensionMatches = ("application/zip".equals(detectedType) && name.endsWith(".zip"))
                || ("application/pdf".equals(detectedType) && name.endsWith(".pdf"))
                || ("application/vnd.rar".equals(detectedType) && name.endsWith(".rar"));
        if (!extensionMatches) {
            throw new JeecgBootException("file extension does not match detected magic");
        }
        String declared = blank(file.getContentType()) ? "application/octet-stream" : file.getContentType().toLowerCase(Locale.ROOT);
        if ("application/octet-stream".equals(declared)) {
            return;
        }
        boolean contentMatches = declared.equals(detectedType)
                || ("application/zip".equals(detectedType)
                && (declared.contains("zip") || "application/x-zip-compressed".equals(declared)))
                || ("application/vnd.rar".equals(detectedType) && declared.contains("rar"));
        if (!contentMatches) {
            throw new JeecgBootException("Content-Type does not match detected magic");
        }
    }

    private void verifyZip(Path path) throws IOException {
        int count = 0;
        long totalUncompressed = 0L;
        long totalCompressed = 0L;
        try (ZipFile zip = ZipFile.builder().setFile(path.toFile()).get()) {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                count++;
                if (count > maxZipEntries) {
                    throw new JeecgBootException("ZIP contains too many entries");
                }
                validateEntry(entry);
                if (entry.isDirectory()) {
                    continue;
                }
                long entrySize = readAndVerifyEntry(zip, entry);
                if (entrySize > maxZipEntryBytes) {
                    throw new JeecgBootException("ZIP entry exceeds maximum uncompressed size");
                }
                totalUncompressed += entrySize;
                totalCompressed += Math.max(0L, entry.getCompressedSize());
                if (totalUncompressed > maxZipTotalBytes) {
                    throw new JeecgBootException("ZIP total uncompressed size exceeds limit");
                }
                enforceRatio(entrySize, entry.getCompressedSize(), "ZIP entry compression ratio exceeds limit");
            }
        }
        enforceRatio(totalUncompressed, totalCompressed, "ZIP total compression ratio exceeds limit");
    }

    private void validateEntry(ZipArchiveEntry entry) {
        String name = entry.getName();
        String normalized = name == null ? "" : name.replace('\\', '/');
        if (normalized.isBlank() || normalized.startsWith("/") || normalized.matches("^[A-Za-z]:/.*")) {
            throw new JeecgBootException("ZIP entry has an invalid absolute path");
        }
        for (String segment : normalized.split("/")) {
            if ("..".equals(segment)) {
                throw new JeecgBootException("ZIP entry path traversal is not allowed");
            }
        }
        if (entry.isUnixSymlink()) {
            throw new JeecgBootException("ZIP symbolic links are not allowed");
        }
        if (entry.getGeneralPurposeBit().usesEncryption()) {
            throw new JeecgBootException("ZIP encrypted entries are not allowed");
        }
    }

    private long readAndVerifyEntry(ZipFile zip, ZipArchiveEntry entry) throws IOException {
        CRC32 crc = new CRC32();
        long size = 0L;
        byte[] buffer = new byte[64 * 1024];
        try (InputStream input = zip.getInputStream(entry)) {
            int read;
            while ((read = input.read(buffer)) != -1) {
                size += read;
                if (size > maxZipEntryBytes) {
                    throw new JeecgBootException("ZIP entry exceeds maximum uncompressed size");
                }
                crc.update(buffer, 0, read);
            }
        }
        if (entry.getCrc() >= 0 && crc.getValue() != entry.getCrc()) {
            throw new JeecgBootException("ZIP entry CRC mismatch");
        }
        return size;
    }

    private void enforceRatio(long uncompressed, long compressed, String message) {
        if (uncompressed <= 0) {
            return;
        }
        if (compressed <= 0 || uncompressed / (double) compressed > maxCompressionRatio) {
            throw new JeecgBootException(message);
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
