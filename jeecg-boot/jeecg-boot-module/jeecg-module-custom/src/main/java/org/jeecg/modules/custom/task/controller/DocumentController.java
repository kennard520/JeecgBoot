package org.jeecg.modules.custom.task.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.custom.task.entity.Document;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.jeecg.modules.custom.task.vo.DocumentParseCallbackVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * 文档解析任务控制器。
 */
@Slf4j
@Tag(name = "文档解析任务")
@RestController
@RequestMapping("/custom/task/document")
public class DocumentController extends JeecgController<Document, IDocumentService> {

    @Autowired
    private IDocumentService documentService;

    @Operation(summary = "分页查询文档解析任务")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/task/document")
    public Result<IPage<Document>> queryPageList(Document document,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        QueryWrapper<Document> queryWrapper = QueryGenerator.initQueryWrapper(document, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<Document> page = new Page<>(pageNo, pageSize);
        IPage<Document> pageList = documentService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    @AutoLog(value = "上传zip文档")
    @Operation(summary = "上传zip文档并创建文档记录")
    @PostMapping(value = "/uploadZip")
    public Result<Document> uploadZip(@RequestParam("file") MultipartFile file) {
        return Result.OK(documentService.uploadZip(file));
    }

    @AutoLog(value = "创建文档解析任务", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "创建文档解析任务")
    @PostMapping(value = "/startParse")
    public Result<Map<String, String>> startParse(@RequestParam(name = "id") Long id) {
        Document document = documentService.startParse(id);
        return Result.OK(Collections.singletonMap("taskId", document.getTaskId()));
    }

    @AutoLog(value = "文档解析任务回调", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "文档解析任务回调")
    @PostMapping(value = "/parseCallback")
    public Result<Document> parseCallback(@RequestBody DocumentParseCallbackVo callback) {
        Document document = Boolean.FALSE.equals(callback.getSuccess())
                ? documentService.failParse(callback.getTaskId(), callback.getErrorMessage())
                : documentService.completeParse(callback.getTaskId(), callback.getDecHeadId());
        return Result.OK(document);
    }

    @AutoLog(value = "新增文档解析任务")
    @Operation(summary = "新增文档解析任务")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Document document) {
        documentService.save(document);
        return Result.OK("添加成功！");
    }

    @AutoLog(value = "编辑文档解析任务", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑文档解析任务")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Document document) {
        documentService.updateById(document);
        return Result.OK("更新成功！");
    }

    @AutoLog(value = "删除文档解析任务")
    @Operation(summary = "通过ID删除文档解析任务")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        documentService.removeById(id);
        return Result.OK("删除成功！");
    }

    @AutoLog(value = "批量删除文档解析任务")
    @Operation(summary = "批量删除文档解析任务")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        documentService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    @Operation(summary = "通过ID查询文档解析任务")
    @GetMapping(value = "/queryById")
    public Result<Document> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                      @RequestParam(name = "id", required = true) String id) {
        Document document = documentService.getById(id);
        return Result.OK(document);
    }

    @Operation(summary = "导出文档解析任务Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Document document) {
        return super.exportXls(request, document, Document.class, "文档解析任务");
    }

    @Operation(summary = "从Excel导入文档解析任务")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Document.class);
    }
}
