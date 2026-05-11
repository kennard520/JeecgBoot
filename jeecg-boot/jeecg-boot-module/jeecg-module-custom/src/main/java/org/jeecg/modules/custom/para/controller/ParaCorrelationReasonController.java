package org.jeecg.modules.custom.para.controller;

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
import org.jeecg.modules.custom.para.entity.ParaCorrelationReason;
import org.jeecg.modules.custom.para.service.IParaCorrelationReasonService;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

/**
 * 关联理由表 ParaCorrelationReason 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；页面关联理由下拉使用本接口的 list 数据。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "关联理由表 ParaCorrelationReason")
@RestController
@RequestMapping("/custom/para/paraCorrelationReason")
public class ParaCorrelationReasonController extends JeecgController<ParaCorrelationReason, IParaCorrelationReasonService> {
    @Autowired
    private IParaCorrelationReasonService paraCorrelationReasonService;

    /**
     * 分页查询 关联理由表。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 CODE 升序返回。</p>
     */
    @Operation(summary = "分页查询关联理由表")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraCorrelationReason")
    public Result<IPage<ParaCorrelationReason>> queryPageList(ParaCorrelationReason paraCorrelationReason,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                              HttpServletRequest req) {
        QueryWrapper<ParaCorrelationReason> queryWrapper = QueryGenerator.initQueryWrapper(paraCorrelationReason, req.getParameterMap());
        queryWrapper.orderByAsc("CODE");
        Page<ParaCorrelationReason> page = new Page<>(pageNo, pageSize);
        IPage<ParaCorrelationReason> pageList = paraCorrelationReasonService.page(page, queryWrapper);
        log.debug("分页查询关联理由表，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 关联理由。
     */
    @AutoLog(value = "新增关联理由")
    @Operation(summary = "新增关联理由")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaCorrelationReason paraCorrelationReason) {
        paraCorrelationReasonService.save(paraCorrelationReason);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 关联理由。
     */
    @AutoLog(value = "编辑关联理由", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑关联理由")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaCorrelationReason paraCorrelationReason) {
        paraCorrelationReasonService.updateById(paraCorrelationReason);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 关联理由。
     */
    @AutoLog(value = "删除关联理由")
    @Operation(summary = "通过ID删除关联理由")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraCorrelationReasonService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 关联理由。
     */
    @AutoLog(value = "批量删除关联理由")
    @Operation(summary = "批量删除关联理由")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraCorrelationReasonService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 关联理由 明细。
     */
    @Operation(summary = "通过ID查询关联理由")
    @GetMapping(value = "/queryById")
    public Result<ParaCorrelationReason> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                                   @RequestParam(name = "id", required = true) String id) {
        ParaCorrelationReason paraCorrelationReason = paraCorrelationReasonService.getById(id);
        return Result.OK(paraCorrelationReason);
    }

    /**
     * 导出 关联理由 Excel。
     */
    @Operation(summary = "导出关联理由Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaCorrelationReason paraCorrelationReason) {
        return super.exportXls(request, paraCorrelationReason, ParaCorrelationReason.class, "关联理由表");
    }

    /**
     * 从 Excel 导入 关联理由。
     */
    @Operation(summary = "从Excel导入关联理由")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaCorrelationReason.class);
    }
}
