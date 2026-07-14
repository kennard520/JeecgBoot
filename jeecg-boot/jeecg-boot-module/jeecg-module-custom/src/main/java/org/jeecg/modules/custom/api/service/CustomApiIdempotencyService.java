package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.exception.CustomApiConflictException;
import org.jeecg.modules.custom.api.mapper.CustomApiFileMapper;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Objects;

@Service
public class CustomApiIdempotencyService {
    private final CustomApiFileMapper fileMapper;
    private final CustomApiTaskMapper taskMapper;

    public CustomApiIdempotencyService(CustomApiFileMapper fileMapper, CustomApiTaskMapper taskMapper) {
        this.fileMapper = fileMapper;
        this.taskMapper = taskMapper;
    }

    public CustomApiFile findFile(Long appId, String clientFileId, String idempotencyKey, String requestHash) {
        if (appId == null) {
            return null;
        }
        String normalizedClientFileId = trimToNull(clientFileId);
        String normalizedIdempotencyKey = trimToNull(idempotencyKey);
        CustomApiFile byClientId = null;
        CustomApiFile byIdempotencyKey = null;
        if (normalizedClientFileId != null) {
            byClientId = fileMapper.selectOne(new LambdaQueryWrapper<CustomApiFile>()
                    .eq(CustomApiFile::getAppId, appId)
                    .eq(CustomApiFile::getClientFileId, normalizedClientFileId));
        }
        if (normalizedIdempotencyKey != null) {
            byIdempotencyKey = fileMapper.selectOne(new LambdaQueryWrapper<CustomApiFile>()
                    .eq(CustomApiFile::getAppId, appId)
                    .eq(CustomApiFile::getIdempotencyKey, normalizedIdempotencyKey));
        }
        verifySameFile(byClientId, byIdempotencyKey);
        return verifyHash(byClientId != null ? byClientId : byIdempotencyKey, requestHash, "file");
    }

    public CustomApiFile insertFileOrFindWinner(CustomApiFile candidate) {
        try {
            fileMapper.insert(candidate);
            return candidate;
        } catch (DataIntegrityViolationException duplicate) {
            if (!isUniqueViolation(duplicate)) {
                throw duplicate;
            }
            CustomApiFile winner = findFileWinner(candidate);
            if (winner == null) {
                throw duplicate;
            }
            return winner;
        }
    }

    public CustomApiTask findTask(Long appId, String clientTaskId, String idempotencyKey, String requestHash) {
        if (appId == null) {
            return null;
        }
        String normalizedClientTaskId = trimToNull(clientTaskId);
        String normalizedIdempotencyKey = trimToNull(idempotencyKey);
        CustomApiTask byClientId = null;
        CustomApiTask byIdempotencyKey = null;
        if (normalizedClientTaskId != null) {
            byClientId = taskMapper.selectOne(new LambdaQueryWrapper<CustomApiTask>()
                    .eq(CustomApiTask::getAppId, appId)
                    .eq(CustomApiTask::getClientTaskId, normalizedClientTaskId));
        }
        if (normalizedIdempotencyKey != null) {
            byIdempotencyKey = taskMapper.selectOne(new LambdaQueryWrapper<CustomApiTask>()
                    .eq(CustomApiTask::getAppId, appId)
                    .eq(CustomApiTask::getIdempotencyKey, normalizedIdempotencyKey));
        }
        verifySameTask(byClientId, byIdempotencyKey);
        return verifyHash(byClientId != null ? byClientId : byIdempotencyKey, requestHash, "task");
    }

    private CustomApiFile findFileWinner(CustomApiFile candidate) {
        for (int attempt = 0; attempt < 3; attempt++) {
            CustomApiFile winner = findFile(candidate.getAppId(), candidate.getClientFileId(),
                    candidate.getIdempotencyKey(), candidate.getRequestHash());
            if (winner != null) {
                return winner;
            }
            try {
                Thread.sleep(10L * (attempt + 1));
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    private void verifySameFile(CustomApiFile left, CustomApiFile right) {
        if (left != null && right != null && !sameRecord(left.getId(), right.getId(), left.getFileId(), right.getFileId())) {
            throw new CustomApiConflictException("clientFileId and idempotency key refer to different files");
        }
    }

    private void verifySameTask(CustomApiTask left, CustomApiTask right) {
        if (left != null && right != null && !sameRecord(left.getId(), right.getId(), left.getTaskId(), right.getTaskId())) {
            throw new CustomApiConflictException("clientTaskId and idempotency key refer to different tasks");
        }
    }

    private boolean sameRecord(Long leftId, Long rightId, String leftBusinessId, String rightBusinessId) {
        if (leftId != null && rightId != null) {
            return leftId.equals(rightId);
        }
        return Objects.equals(leftBusinessId, rightBusinessId);
    }

    private boolean isUniqueViolation(DataIntegrityViolationException error) {
        if (error instanceof DuplicateKeyException) {
            return true;
        }
        Throwable current = error;
        while (current != null) {
            if (current instanceof SQLException sql
                    && (sql.getErrorCode() == -6612 || "23000".equals(sql.getSQLState()))) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private <T> T verifyHash(T existing, String requestHash, String resource) {
        if (existing == null) {
            return null;
        }
        String existingHash = existing instanceof CustomApiFile
                ? ((CustomApiFile) existing).getRequestHash()
                : ((CustomApiTask) existing).getRequestHash();
        if (existingHash == null || !existingHash.equals(requestHash)) {
            throw new CustomApiConflictException(resource + " idempotency key was already used with different parameters");
        }
        return existing;
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        return blank(value) ? null : value.trim();
    }
}
