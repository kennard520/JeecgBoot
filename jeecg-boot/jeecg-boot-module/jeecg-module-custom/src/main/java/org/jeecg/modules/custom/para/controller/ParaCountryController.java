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
import org.jeecg.modules.custom.para.entity.ParaCountry;
import org.jeecg.modules.custom.para.service.IParaCountryService;
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
 * 国别(地区) ParaCountry 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "国别(地区) ParaCountry")
@RestController
@RequestMapping("/custom/para/paraCountry")
public class ParaCountryController extends JeecgController<ParaCountry, IParaCountryService> {
    @Autowired
    private IParaCountryService paraCountryService;

    /**
     * 分页查询 国别(地区)。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询国别(地区)")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraCountry")
    public Result<IPage<ParaCountry>> queryPageList(ParaCountry paraCountry,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaCountry> queryWrapper = QueryGenerator.initQueryWrapper(paraCountry, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaCountry> page = new Page<>(pageNo, pageSize);
        IPage<ParaCountry> pageList = paraCountryService.page(page, queryWrapper);
        log.debug("分页查询国别(地区)，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 国别(地区)。
     */
    @AutoLog(value = "新增国别(地区)")
    @Operation(summary = "新增国别(地区)")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaCountry paraCountry) {
        paraCountryService.save(paraCountry);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 国别(地区)。
     */
    @AutoLog(value = "编辑国别(地区)", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑国别(地区)")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaCountry paraCountry) {
        paraCountryService.updateById(paraCountry);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 国别(地区)。
     */
    @AutoLog(value = "删除国别(地区)")
    @Operation(summary = "通过ID删除国别(地区)")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraCountryService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 国别(地区)。
     */
    @AutoLog(value = "批量删除国别(地区)")
    @Operation(summary = "批量删除国别(地区)")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraCountryService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 国别(地区) 明细。
     */
    @Operation(summary = "通过ID查询国别(地区)")
    @GetMapping(value = "/queryById")
    public Result<ParaCountry> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaCountry paraCountry = paraCountryService.getById(id);
        return Result.OK(paraCountry);
    }

    /**
     * 导出 国别(地区) Excel。
     */
    @Operation(summary = "导出国别(地区)Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaCountry paraCountry) {
        return super.exportXls(request, paraCountry, ParaCountry.class, "国别(地区)");
    }

    /**
     * 从 Excel 导入 国别(地区)。
     */
    @Operation(summary = "从Excel导入国别(地区)")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaCountry.class);
    }
}
