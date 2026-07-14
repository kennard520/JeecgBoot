package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.exception.CustomApiConflictException;
import org.jeecg.modules.custom.api.mapper.CustomApiFileMapper;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
        CustomApiFile existing = null;
        if (!blank(clientFileId)) {
            existing = fileMapper.selectOne(new LambdaQueryWrapper<CustomApiFile>()
                    .eq(CustomApiFile::getAppId, appId)
                    .eq(CustomApiFile::getClientFileId, clientFileId.trim()));
        }
        if (existing == null && !blank(idempotencyKey)) {
            existing = fileMapper.selectOne(new LambdaQueryWrapper<CustomApiFile>()
                    .eq(CustomApiFile::getAppId, appId)
                    .eq(CustomApiFile::getIdempotencyKey, idempotencyKey.trim()));
        }
        return verifyHash(existing, requestHash, "file");
    }

    public CustomApiFile insertFileOrFindWinner(CustomApiFile candidate) {
        try {
            fileMapper.insert(candidate);
            return candidate;
        } catch (DuplicateKeyException duplicate) {
            CustomApiFile winner = findFile(candidate.getAppId(), candidate.getClientFileId(),
                    candidate.getIdempotencyKey(), candidate.getRequestHash());
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
        CustomApiTask existing = null;
        if (!blank(clientTaskId)) {
            existing = taskMapper.selectOne(new LambdaQueryWrapper<CustomApiTask>()
                    .eq(CustomApiTask::getAppId, appId)
                    .eq(CustomApiTask::getClientTaskId, clientTaskId.trim()));
        }
        if (existing == null && !blank(idempotencyKey)) {
            existing = taskMapper.selectOne(new LambdaQueryWrapper<CustomApiTask>()
                    .eq(CustomApiTask::getAppId, appId)
                    .eq(CustomApiTask::getIdempotencyKey, idempotencyKey.trim()));
        }
        return verifyHash(existing, requestHash, "task");
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
}
