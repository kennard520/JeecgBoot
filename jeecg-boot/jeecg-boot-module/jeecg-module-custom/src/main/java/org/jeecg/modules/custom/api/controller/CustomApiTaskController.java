package org.jeecg.modules.custom.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.jeecg.modules.custom.api.service.ICustomApiTaskService;
import org.jeecg.modules.custom.api.service.CustomApiRateLimiter;
import org.jeecg.modules.custom.api.vo.TaskCreateRequest;
import org.jeecg.modules.custom.api.vo.TaskResponse;
import org.jeecg.modules.custom.api.vo.TaskResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customs AI external tasks")
@RestController
@RequestMapping("/custom/api/tasks")
public class CustomApiTaskController {

    @Autowired
    private ICustomApiAppService appService;
    @Autowired
    private ICustomApiTaskService taskService;
    @Autowired
    private CustomApiRateLimiter rateLimiter;

    @IgnoreAuth
    @Operation(summary = "Create parse task")
    @PostMapping
    public Result<TaskResponse> create(
            @RequestBody TaskCreateRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            HttpServletRequest servletRequest) {
        CustomApiApp app = appService.requireApp(servletRequest);
        rateLimiter.check(app, "task-create");
        return Result.OK(taskService.createTask(app, request, idempotencyKey));
    }

    @IgnoreAuth
    @Operation(summary = "Get parse task status")
    @GetMapping("/{taskId}")
    public Result<TaskResponse> get(@PathVariable String taskId, HttpServletRequest servletRequest) {
        CustomApiApp app = appService.requireApp(servletRequest);
        rateLimiter.check(app, "poll");
        return Result.OK(taskService.getTask(app, taskId));
    }

    @IgnoreAuth
    @Operation(summary = "Get parse result")
    @GetMapping("/{taskId}/result")
    public Result<TaskResultResponse> result(@PathVariable String taskId, HttpServletRequest servletRequest) {
        CustomApiApp app = appService.requireApp(servletRequest);
        rateLimiter.check(app, "poll");
        return Result.OK(taskService.getResult(app, taskId));
    }
}
