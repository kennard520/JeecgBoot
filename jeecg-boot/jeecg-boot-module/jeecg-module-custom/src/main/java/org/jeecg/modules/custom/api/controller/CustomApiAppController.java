package org.jeecg.modules.custom.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.jeecg.modules.custom.api.vo.CustomApiAppResponse;
import org.jeecg.modules.custom.api.vo.CustomApiAppSaveRequest;
import org.jeecg.modules.custom.api.vo.CustomApiAppSecretResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Tag(name = "Customs AI API app management")
@RestController
@RequestMapping("/custom/api/app")
public class CustomApiAppController extends JeecgController<CustomApiApp, ICustomApiAppService> {

    @Autowired
    private ICustomApiAppService appService;

    @Operation(summary = "Page query external API apps")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/api/app")
    public Result<IPage<CustomApiAppResponse>> queryPageList(CustomApiApp app,
                                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             HttpServletRequest req) {
        app.setAppSecretHash(null);
        app.setAccessTokenHash(null);
        QueryWrapper<CustomApiApp> queryWrapper = QueryGenerator.initQueryWrapper(app, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<CustomApiApp> page = new Page<>(pageNo, pageSize);
        IPage<CustomApiAppResponse> pageList = appService.page(page, queryWrapper).convert(CustomApiAppResponse::fromEntity);
        return Result.OK(pageList);
    }

    @AutoLog(value = "Create external API app")
    @Operation(summary = "Create external API app")
    @PostMapping(value = "/add")
    public Result<CustomApiAppSecretResponse> add(@RequestBody CustomApiAppSaveRequest request) {
        return Result.OK(appService.createApp(request));
    }

    @AutoLog(value = "Update external API app", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "Update external API app")
    @PutMapping(value = "/edit")
    public Result<CustomApiAppResponse> edit(@RequestBody CustomApiAppSaveRequest request) {
        return Result.OK(appService.updateApp(request));
    }

    @AutoLog(value = "Reset external API app secret", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "Reset external API app secret")
    @PostMapping(value = "/resetSecret")
    public Result<CustomApiAppSecretResponse> resetSecret(@RequestParam(name = "id") Long id) {
        return Result.OK(appService.resetSecret(id));
    }

    @AutoLog(value = "Clear external API access token", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "Clear external API access token")
    @PostMapping(value = "/clearAccessToken")
    public Result<CustomApiAppResponse> clearAccessToken(@RequestParam(name = "id") Long id) {
        return Result.OK(appService.clearAccessToken(id));
    }

    @AutoLog(value = "Delete external API app")
    @Operation(summary = "Delete external API app by id")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        appService.removeById(id);
        return Result.OK("删除成功");
    }

    @AutoLog(value = "Batch delete external API apps")
    @Operation(summary = "Batch delete external API apps")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        appService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功");
    }

    @Operation(summary = "Query external API app by id")
    @GetMapping(value = "/queryById")
    public Result<CustomApiAppResponse> queryById(@Parameter(name = "id", description = "id", required = true)
                                                  @RequestParam(name = "id", required = true) String id) {
        return Result.OK(CustomApiAppResponse.fromEntity(appService.getById(id)));
    }

    @Operation(summary = "Check appKey uniqueness")
    @GetMapping(value = "/checkAppKey")
    public Result<Map<String, Boolean>> checkAppKey(@RequestParam(name = "appKey") String appKey,
                                                    @RequestParam(name = "id", required = false) Long id) {
        return Result.OK(Collections.singletonMap("exists", appService.appKeyExists(appKey, id)));
    }
}
