package org.jeecg.modules.custom.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalDownloadTokenServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-07-14T05:00:00Z"), ZoneOffset.UTC);

    @Test
    void issuesTaskFileRunBoundShortLivedUrl() {
        InternalDownloadTokenService service = service();

        InternalDownloadTokenService.DownloadGrant grant =
                service.issue("task-1", "file-1", 2);

        assertThat(grant.expiresAt()).isEqualTo(1784005500L);
        assertThat(grant.url()).startsWith(
                "https://entry.example/jeecgboot/custom/api/internal/tasks/task-1/files/file-1/download?");
        assertThat(grant.url()).contains("runNo=2", "expires=1784005500", "signature=");
        service.validate("task-1", "file-1", 2, grant.expiresAt(), grant.signature());
    }

    @Test
    void failsFastWithoutExplicitBaseUrlOrSecret() {
        assertThatThrownBy(() -> new InternalDownloadTokenService(
                "", "x".repeat(32), 300, CLOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("internal-base-url");
        assertThatThrownBy(() -> new InternalDownloadTokenService(
                "https://entry.example", "", 300, CLOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("internal-download-secret");
    }

    @Test
    void ttlMustCoverConfiguredBrokerQueueWindow() {
        assertThatThrownBy(() -> new InternalDownloadTokenService(
                "https://entry.example", "s".repeat(32), 300, 600, CLOCK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("queue");

        InternalDownloadTokenService service = new InternalDownloadTokenService(
                "https://entry.example", "s".repeat(32), 900, 600, CLOCK);
        assertThat(service.issue("task-1", "file-1", 1).expiresAt())
                .isEqualTo(1784006100L);
    }

    @Test
    void rejectsExpiredGrantAsUnauthorized() {
        InternalDownloadTokenService issuer = service();
        InternalDownloadTokenService.DownloadGrant grant =
                issuer.issue("task-1", "file-1", 1);
        InternalDownloadTokenService verifier = new InternalDownloadTokenService(
                "https://entry.example/jeecgboot", "s".repeat(32), 300,
                Clock.fixed(Instant.parse("2026-07-14T05:06:00Z"), ZoneOffset.UTC));

        assertThatThrownBy(() -> verifier.validate(
                "task-1", "file-1", 1, grant.expiresAt(), grant.signature()))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void rejectsTamperedTaskFileOrRunAsForbidden() {
        InternalDownloadTokenService service = service();
        InternalDownloadTokenService.DownloadGrant grant =
                service.issue("task-1", "file-1", 1);

        assertThatThrownBy(() -> service.validate(
                "task-1", "file-1", 2, grant.expiresAt(), grant.signature()))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    private InternalDownloadTokenService service() {
        return new InternalDownloadTokenService(
                "https://entry.example/jeecgboot/", "s".repeat(32), 300, CLOCK);
    }
}
