package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.exception.CustomApiConflictException;
import org.jeecg.modules.custom.api.mapper.CustomApiFileMapper;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomApiIdempotencyServiceTest {

    private final CustomApiFileMapper fileMapper = mock(CustomApiFileMapper.class);
    private final CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
    private final CustomApiIdempotencyService service = new CustomApiIdempotencyService(fileMapper, taskMapper);

    @Test
    void successfulFileInsertReturnsCandidate() {
        CustomApiFile candidate = candidate("hash-1");
        when(fileMapper.insert(candidate)).thenReturn(1);

        assertThat(service.insertFileOrFindWinner(candidate)).isSameAs(candidate);

        verify(fileMapper).insert(candidate);
    }

    @Test
    void duplicateFileInsertReturnsConcurrentWinnerWhenHashMatches() {
        CustomApiFile candidate = candidate("hash-1");
        CustomApiFile winner = candidate("hash-1").setId(77L).setFileId("file-winner");
        when(fileMapper.insert(candidate)).thenThrow(new DuplicateKeyException("concurrent insert"));
        when(fileMapper.selectOne(any())).thenReturn(winner);

        assertThat(service.insertFileOrFindWinner(candidate)).isSameAs(winner);
    }

    @Test
    void duplicateFileInsertBecomesConflictWhenWinnerHashDiffers() {
        CustomApiFile candidate = candidate("hash-1");
        CustomApiFile winner = candidate("hash-2").setId(77L).setFileId("file-winner");
        when(fileMapper.insert(candidate)).thenThrow(new DuplicateKeyException("concurrent insert"));
        when(fileMapper.selectOne(any())).thenReturn(winner);

        assertThatThrownBy(() -> service.insertFileOrFindWinner(candidate))
                .isInstanceOf(CustomApiConflictException.class)
                .hasMessageContaining("different parameters");
    }

    private CustomApiFile candidate(String requestHash) {
        return new CustomApiFile()
                .setAppId(9L)
                .setFileId("file-candidate")
                .setClientFileId("client-file-1")
                .setIdempotencyKey("idem-file-1")
                .setRequestHash(requestHash);
    }
}
