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
import org.jeecg.modules.custom.para.entity.ParaPortLin;
import org.jeecg.modules.custom.para.service.IParaPortLinService;
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
 * 港口 ParaPortLin 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "港口 ParaPortLin")
@RestController
@RequestMapping("/custom/para/paraPortLin")
public class ParaPortLinController extends JeecgController<ParaPortLin, IParaPortLinService> {
    @Autowired
    private IParaPortLinService paraPortLinService;

    /**
     * 分页查询 港口。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询港口")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraPortLin")
    public Result<IPage<ParaPortLin>> queryPageList(ParaPortLin paraPortLin,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaPortLin> queryWrapper = QueryGenerator.initQueryWrapper(paraPortLin, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaPortLin> page = new Page<>(pageNo, pageSize);
        IPage<ParaPortLin> pageList = paraPortLinService.page(page, queryWrapper);
        log.debug("分页查询港口，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 港口。
     */
    @AutoLog(value = "新增港口")
    @Operation(summary = "新增港口")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaPortLin paraPortLin) {
        paraPortLinService.save(paraPortLin);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 港口。
     */
    @AutoLog(value = "编辑港口", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑港口")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaPortLin paraPortLin) {
        paraPortLinService.updateById(paraPortLin);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 港口。
     */
    @AutoLog(value = "删除港口")
    @Operation(summary = "通过ID删除港口")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraPortLinService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 港口。
     */
    @AutoLog(value = "批量删除港口")
    @Operation(summary = "批量删除港口")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraPortLinService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 港口 明细。
     */
    @Operation(summary = "通过ID查询港口")
    @GetMapping(value = "/queryById")
    public Result<ParaPortLin> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaPortLin paraPortLin = paraPortLinService.getById(id);
        return Result.OK(paraPortLin);
    }

    /**
     * 导出 港口 Excel。
     */
    @Operation(summary = "导出港口Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaPortLin paraPortLin) {
        return super.exportXls(request, paraPortLin, ParaPortLin.class, "港口");
    }

    /**
     * 从 Excel 导入 港口。
     */
    @Operation(summary = "从Excel导入港口")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaPortLin.class);
    }
}
