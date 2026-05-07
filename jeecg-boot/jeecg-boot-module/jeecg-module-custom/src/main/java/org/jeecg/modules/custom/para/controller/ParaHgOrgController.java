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
import org.jeecg.modules.custom.para.entity.ParaHgOrg;
import org.jeecg.modules.custom.para.service.IParaHgOrgService;
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
 * 海关行政机关 ParaHgOrg 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "海关行政机关 ParaHgOrg")
@RestController
@RequestMapping("/custom/para/paraHgOrg")
public class ParaHgOrgController extends JeecgController<ParaHgOrg, IParaHgOrgService> {
    @Autowired
    private IParaHgOrgService paraHgOrgService;

    /**
     * 分页查询 海关行政机关。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询海关行政机关")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraHgOrg")
    public Result<IPage<ParaHgOrg>> queryPageList(ParaHgOrg paraHgOrg,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaHgOrg> queryWrapper = QueryGenerator.initQueryWrapper(paraHgOrg, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaHgOrg> page = new Page<>(pageNo, pageSize);
        IPage<ParaHgOrg> pageList = paraHgOrgService.page(page, queryWrapper);
        log.debug("分页查询海关行政机关，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 海关行政机关。
     */
    @AutoLog(value = "新增海关行政机关")
    @Operation(summary = "新增海关行政机关")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaHgOrg paraHgOrg) {
        paraHgOrgService.save(paraHgOrg);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 海关行政机关。
     */
    @AutoLog(value = "编辑海关行政机关", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑海关行政机关")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaHgOrg paraHgOrg) {
        paraHgOrgService.updateById(paraHgOrg);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 海关行政机关。
     */
    @AutoLog(value = "删除海关行政机关")
    @Operation(summary = "通过ID删除海关行政机关")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraHgOrgService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 海关行政机关。
     */
    @AutoLog(value = "批量删除海关行政机关")
    @Operation(summary = "批量删除海关行政机关")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraHgOrgService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 海关行政机关 明细。
     */
    @Operation(summary = "通过ID查询海关行政机关")
    @GetMapping(value = "/queryById")
    public Result<ParaHgOrg> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaHgOrg paraHgOrg = paraHgOrgService.getById(id);
        return Result.OK(paraHgOrg);
    }

    /**
     * 导出 海关行政机关 Excel。
     */
    @Operation(summary = "导出海关行政机关Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaHgOrg paraHgOrg) {
        return super.exportXls(request, paraHgOrg, ParaHgOrg.class, "海关行政机关");
    }

    /**
     * 从 Excel 导入 海关行政机关。
     */
    @Operation(summary = "从Excel导入海关行政机关")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaHgOrg.class);
    }
}
