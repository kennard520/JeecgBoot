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
import org.jeecg.modules.custom.para.entity.ParaMerchElement20250715;
import org.jeecg.modules.custom.para.service.IParaMerchElement20250715Service;
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
 * PARA_MERCH_ELEMENT_20250715 ParaMerchElement20250715 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "PARA_MERCH_ELEMENT_20250715 ParaMerchElement20250715")
@RestController
@RequestMapping("/custom/para/paraMerchElement20250715")
public class ParaMerchElement20250715Controller extends JeecgController<ParaMerchElement20250715, IParaMerchElement20250715Service> {
    @Autowired
    private IParaMerchElement20250715Service paraMerchElement20250715Service;

    /**
     * 分页查询 PARA_MERCH_ELEMENT_20250715。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询PARA_MERCH_ELEMENT_20250715")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraMerchElement20250715")
    public Result<IPage<ParaMerchElement20250715>> queryPageList(ParaMerchElement20250715 paraMerchElement20250715,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaMerchElement20250715> queryWrapper = QueryGenerator.initQueryWrapper(paraMerchElement20250715, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaMerchElement20250715> page = new Page<>(pageNo, pageSize);
        IPage<ParaMerchElement20250715> pageList = paraMerchElement20250715Service.page(page, queryWrapper);
        log.debug("分页查询PARA_MERCH_ELEMENT_20250715，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 PARA_MERCH_ELEMENT_20250715。
     */
    @AutoLog(value = "新增PARA_MERCH_ELEMENT_20250715")
    @Operation(summary = "新增PARA_MERCH_ELEMENT_20250715")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaMerchElement20250715 paraMerchElement20250715) {
        paraMerchElement20250715Service.save(paraMerchElement20250715);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 PARA_MERCH_ELEMENT_20250715。
     */
    @AutoLog(value = "编辑PARA_MERCH_ELEMENT_20250715", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑PARA_MERCH_ELEMENT_20250715")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaMerchElement20250715 paraMerchElement20250715) {
        paraMerchElement20250715Service.updateById(paraMerchElement20250715);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 PARA_MERCH_ELEMENT_20250715。
     */
    @AutoLog(value = "删除PARA_MERCH_ELEMENT_20250715")
    @Operation(summary = "通过ID删除PARA_MERCH_ELEMENT_20250715")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraMerchElement20250715Service.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 PARA_MERCH_ELEMENT_20250715。
     */
    @AutoLog(value = "批量删除PARA_MERCH_ELEMENT_20250715")
    @Operation(summary = "批量删除PARA_MERCH_ELEMENT_20250715")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraMerchElement20250715Service.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 PARA_MERCH_ELEMENT_20250715 明细。
     */
    @Operation(summary = "通过ID查询PARA_MERCH_ELEMENT_20250715")
    @GetMapping(value = "/queryById")
    public Result<ParaMerchElement20250715> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaMerchElement20250715 paraMerchElement20250715 = paraMerchElement20250715Service.getById(id);
        return Result.OK(paraMerchElement20250715);
    }

    /**
     * 导出 PARA_MERCH_ELEMENT_20250715 Excel。
     */
    @Operation(summary = "导出PARA_MERCH_ELEMENT_20250715Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaMerchElement20250715 paraMerchElement20250715) {
        return super.exportXls(request, paraMerchElement20250715, ParaMerchElement20250715.class, "PARA_MERCH_ELEMENT_20250715");
    }

    /**
     * 从 Excel 导入 PARA_MERCH_ELEMENT_20250715。
     */
    @Operation(summary = "从Excel导入PARA_MERCH_ELEMENT_20250715")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaMerchElement20250715.class);
    }
}
