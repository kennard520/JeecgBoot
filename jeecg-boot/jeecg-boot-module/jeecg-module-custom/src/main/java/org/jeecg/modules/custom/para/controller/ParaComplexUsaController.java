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
import org.jeecg.modules.custom.para.entity.ParaComplexUsa;
import org.jeecg.modules.custom.para.service.IParaComplexUsaService;
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
 * 青岛海关对美加征税率表 ParaComplexUsa 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "青岛海关对美加征税率表 ParaComplexUsa")
@RestController
@RequestMapping("/custom/para/paraComplexUsa")
public class ParaComplexUsaController extends JeecgController<ParaComplexUsa, IParaComplexUsaService> {
    @Autowired
    private IParaComplexUsaService paraComplexUsaService;

    /**
     * 分页查询 青岛海关对美加征税率表。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 CODE_T_S 倒序返回。</p>
     */
    @Operation(summary = "分页查询青岛海关对美加征税率表")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraComplexUsa")
    public Result<IPage<ParaComplexUsa>> queryPageList(ParaComplexUsa paraComplexUsa,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaComplexUsa> queryWrapper = QueryGenerator.initQueryWrapper(paraComplexUsa, req.getParameterMap());
        queryWrapper.orderByDesc("CODE_T_S");
        Page<ParaComplexUsa> page = new Page<>(pageNo, pageSize);
        IPage<ParaComplexUsa> pageList = paraComplexUsaService.page(page, queryWrapper);
        log.debug("分页查询青岛海关对美加征税率表，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 青岛海关对美加征税率表。
     */
    @AutoLog(value = "新增青岛海关对美加征税率表")
    @Operation(summary = "新增青岛海关对美加征税率表")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaComplexUsa paraComplexUsa) {
        paraComplexUsaService.save(paraComplexUsa);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 青岛海关对美加征税率表。
     */
    @AutoLog(value = "编辑青岛海关对美加征税率表", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑青岛海关对美加征税率表")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaComplexUsa paraComplexUsa) {
        paraComplexUsaService.updateById(paraComplexUsa);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 青岛海关对美加征税率表。
     */
    @AutoLog(value = "删除青岛海关对美加征税率表")
    @Operation(summary = "通过ID删除青岛海关对美加征税率表")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraComplexUsaService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 青岛海关对美加征税率表。
     */
    @AutoLog(value = "批量删除青岛海关对美加征税率表")
    @Operation(summary = "批量删除青岛海关对美加征税率表")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraComplexUsaService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 青岛海关对美加征税率表 明细。
     */
    @Operation(summary = "通过ID查询青岛海关对美加征税率表")
    @GetMapping(value = "/queryById")
    public Result<ParaComplexUsa> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaComplexUsa paraComplexUsa = paraComplexUsaService.getById(id);
        return Result.OK(paraComplexUsa);
    }

    /**
     * 导出 青岛海关对美加征税率表 Excel。
     */
    @Operation(summary = "导出青岛海关对美加征税率表Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaComplexUsa paraComplexUsa) {
        return super.exportXls(request, paraComplexUsa, ParaComplexUsa.class, "青岛海关对美加征税率表");
    }

    /**
     * 从 Excel 导入 青岛海关对美加征税率表。
     */
    @Operation(summary = "从Excel导入青岛海关对美加征税率表")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaComplexUsa.class);
    }
}
