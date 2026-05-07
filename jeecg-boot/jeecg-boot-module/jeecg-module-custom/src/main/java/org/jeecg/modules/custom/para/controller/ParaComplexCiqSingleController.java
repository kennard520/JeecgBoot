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
import org.jeecg.modules.custom.para.entity.ParaComplexCiqSingle;
import org.jeecg.modules.custom.para.service.IParaComplexCiqSingleService;
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
 * PARA_COMPLEX_CIQ_SINGLE ParaComplexCiqSingle 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "PARA_COMPLEX_CIQ_SINGLE ParaComplexCiqSingle")
@RestController
@RequestMapping("/custom/para/paraComplexCiqSingle")
public class ParaComplexCiqSingleController extends JeecgController<ParaComplexCiqSingle, IParaComplexCiqSingleService> {
    @Autowired
    private IParaComplexCiqSingleService paraComplexCiqSingleService;

    /**
     * 分页查询 PARA_COMPLEX_CIQ_SINGLE。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 STATCODE 倒序返回。</p>
     */
    @Operation(summary = "分页查询PARA_COMPLEX_CIQ_SINGLE")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraComplexCiqSingle")
    public Result<IPage<ParaComplexCiqSingle>> queryPageList(ParaComplexCiqSingle paraComplexCiqSingle,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaComplexCiqSingle> queryWrapper = QueryGenerator.initQueryWrapper(paraComplexCiqSingle, req.getParameterMap());
        queryWrapper.orderByDesc("STATCODE");
        Page<ParaComplexCiqSingle> page = new Page<>(pageNo, pageSize);
        IPage<ParaComplexCiqSingle> pageList = paraComplexCiqSingleService.page(page, queryWrapper);
        log.debug("分页查询PARA_COMPLEX_CIQ_SINGLE，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 PARA_COMPLEX_CIQ_SINGLE。
     */
    @AutoLog(value = "新增PARA_COMPLEX_CIQ_SINGLE")
    @Operation(summary = "新增PARA_COMPLEX_CIQ_SINGLE")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaComplexCiqSingle paraComplexCiqSingle) {
        paraComplexCiqSingleService.save(paraComplexCiqSingle);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 PARA_COMPLEX_CIQ_SINGLE。
     */
    @AutoLog(value = "编辑PARA_COMPLEX_CIQ_SINGLE", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑PARA_COMPLEX_CIQ_SINGLE")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaComplexCiqSingle paraComplexCiqSingle) {
        paraComplexCiqSingleService.updateById(paraComplexCiqSingle);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 PARA_COMPLEX_CIQ_SINGLE。
     */
    @AutoLog(value = "删除PARA_COMPLEX_CIQ_SINGLE")
    @Operation(summary = "通过ID删除PARA_COMPLEX_CIQ_SINGLE")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraComplexCiqSingleService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 PARA_COMPLEX_CIQ_SINGLE。
     */
    @AutoLog(value = "批量删除PARA_COMPLEX_CIQ_SINGLE")
    @Operation(summary = "批量删除PARA_COMPLEX_CIQ_SINGLE")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraComplexCiqSingleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 PARA_COMPLEX_CIQ_SINGLE 明细。
     */
    @Operation(summary = "通过ID查询PARA_COMPLEX_CIQ_SINGLE")
    @GetMapping(value = "/queryById")
    public Result<ParaComplexCiqSingle> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaComplexCiqSingle paraComplexCiqSingle = paraComplexCiqSingleService.getById(id);
        return Result.OK(paraComplexCiqSingle);
    }

    /**
     * 导出 PARA_COMPLEX_CIQ_SINGLE Excel。
     */
    @Operation(summary = "导出PARA_COMPLEX_CIQ_SINGLEExcel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaComplexCiqSingle paraComplexCiqSingle) {
        return super.exportXls(request, paraComplexCiqSingle, ParaComplexCiqSingle.class, "PARA_COMPLEX_CIQ_SINGLE");
    }

    /**
     * 从 Excel 导入 PARA_COMPLEX_CIQ_SINGLE。
     */
    @Operation(summary = "从Excel导入PARA_COMPLEX_CIQ_SINGLE")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaComplexCiqSingle.class);
    }
}
