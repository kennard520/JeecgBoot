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
import org.jeecg.modules.custom.para.entity.ParaExchrateRelTemp;
import org.jeecg.modules.custom.para.service.IParaExchrateRelTempService;
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
 * PARA_EXCHRATE_REL_TEMP ParaExchrateRelTemp 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "PARA_EXCHRATE_REL_TEMP ParaExchrateRelTemp")
@RestController
@RequestMapping("/custom/para/paraExchrateRelTemp")
public class ParaExchrateRelTempController extends JeecgController<ParaExchrateRelTemp, IParaExchrateRelTempService> {
    @Autowired
    private IParaExchrateRelTempService paraExchrateRelTempService;

    /**
     * 分页查询 PARA_EXCHRATE_REL_TEMP。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 BEGIN_DATE 倒序返回。</p>
     */
    @Operation(summary = "分页查询PARA_EXCHRATE_REL_TEMP")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraExchrateRelTemp")
    public Result<IPage<ParaExchrateRelTemp>> queryPageList(ParaExchrateRelTemp paraExchrateRelTemp,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaExchrateRelTemp> queryWrapper = QueryGenerator.initQueryWrapper(paraExchrateRelTemp, req.getParameterMap());
        queryWrapper.orderByDesc("BEGIN_DATE");
        Page<ParaExchrateRelTemp> page = new Page<>(pageNo, pageSize);
        IPage<ParaExchrateRelTemp> pageList = paraExchrateRelTempService.page(page, queryWrapper);
        log.debug("分页查询PARA_EXCHRATE_REL_TEMP，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 PARA_EXCHRATE_REL_TEMP。
     */
    @AutoLog(value = "新增PARA_EXCHRATE_REL_TEMP")
    @Operation(summary = "新增PARA_EXCHRATE_REL_TEMP")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaExchrateRelTemp paraExchrateRelTemp) {
        paraExchrateRelTempService.save(paraExchrateRelTemp);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 PARA_EXCHRATE_REL_TEMP。
     */
    @AutoLog(value = "编辑PARA_EXCHRATE_REL_TEMP", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑PARA_EXCHRATE_REL_TEMP")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaExchrateRelTemp paraExchrateRelTemp) {
        paraExchrateRelTempService.updateById(paraExchrateRelTemp);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 PARA_EXCHRATE_REL_TEMP。
     */
    @AutoLog(value = "删除PARA_EXCHRATE_REL_TEMP")
    @Operation(summary = "通过ID删除PARA_EXCHRATE_REL_TEMP")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraExchrateRelTempService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 PARA_EXCHRATE_REL_TEMP。
     */
    @AutoLog(value = "批量删除PARA_EXCHRATE_REL_TEMP")
    @Operation(summary = "批量删除PARA_EXCHRATE_REL_TEMP")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraExchrateRelTempService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 PARA_EXCHRATE_REL_TEMP 明细。
     */
    @Operation(summary = "通过ID查询PARA_EXCHRATE_REL_TEMP")
    @GetMapping(value = "/queryById")
    public Result<ParaExchrateRelTemp> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaExchrateRelTemp paraExchrateRelTemp = paraExchrateRelTempService.getById(id);
        return Result.OK(paraExchrateRelTemp);
    }

    /**
     * 导出 PARA_EXCHRATE_REL_TEMP Excel。
     */
    @Operation(summary = "导出PARA_EXCHRATE_REL_TEMPExcel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaExchrateRelTemp paraExchrateRelTemp) {
        return super.exportXls(request, paraExchrateRelTemp, ParaExchrateRelTemp.class, "PARA_EXCHRATE_REL_TEMP");
    }

    /**
     * 从 Excel 导入 PARA_EXCHRATE_REL_TEMP。
     */
    @Operation(summary = "从Excel导入PARA_EXCHRATE_REL_TEMP")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaExchrateRelTemp.class);
    }
}
