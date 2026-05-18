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
import org.jeecg.modules.custom.para.entity.ParaGoodsAttr;
import org.jeecg.modules.custom.para.service.IParaGoodsAttrService;
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
 * 货物属性 ParaGoodsAttr 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "货物属性 ParaGoodsAttr")
@RestController
@RequestMapping("/custom/para/paraGoodsAttr")
public class ParaGoodsAttrController extends JeecgController<ParaGoodsAttr, IParaGoodsAttrService> {
    @Autowired
    private IParaGoodsAttrService paraGoodsAttrService;

    /**
     * 分页查询 货物属性。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按货物属性代码升序返回。</p>
     */
    @Operation(summary = "分页查询货物属性")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraGoodsAttr")
    public Result<IPage<ParaGoodsAttr>> queryPageList(ParaGoodsAttr paraGoodsAttr,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        QueryWrapper<ParaGoodsAttr> queryWrapper = QueryGenerator.initQueryWrapper(paraGoodsAttr, req.getParameterMap());
        queryWrapper.orderByAsc("ATTR_CODE");
        Page<ParaGoodsAttr> page = new Page<>(pageNo, pageSize);
        IPage<ParaGoodsAttr> pageList = paraGoodsAttrService.page(page, queryWrapper);
        log.debug("分页查询货物属性，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 货物属性。
     */
    @AutoLog(value = "新增货物属性")
    @Operation(summary = "新增货物属性")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaGoodsAttr paraGoodsAttr) {
        paraGoodsAttrService.save(paraGoodsAttr);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 货物属性。
     */
    @AutoLog(value = "编辑货物属性", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑货物属性")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaGoodsAttr paraGoodsAttr) {
        paraGoodsAttrService.updateById(paraGoodsAttr);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 货物属性。
     */
    @AutoLog(value = "删除货物属性")
    @Operation(summary = "通过ID删除货物属性")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraGoodsAttrService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 货物属性。
     */
    @AutoLog(value = "批量删除货物属性")
    @Operation(summary = "批量删除货物属性")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraGoodsAttrService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 货物属性 明细。
     */
    @Operation(summary = "通过ID查询货物属性")
    @GetMapping(value = "/queryById")
    public Result<ParaGoodsAttr> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                           @RequestParam(name = "id", required = true) String id) {
        ParaGoodsAttr paraGoodsAttr = paraGoodsAttrService.getById(id);
        return Result.OK(paraGoodsAttr);
    }

    /**
     * 导出 货物属性 Excel。
     */
    @Operation(summary = "导出货物属性Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaGoodsAttr paraGoodsAttr) {
        return super.exportXls(request, paraGoodsAttr, ParaGoodsAttr.class, "货物属性");
    }

    /**
     * 从 Excel 导入 货物属性。
     */
    @Operation(summary = "从Excel导入货物属性")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaGoodsAttr.class);
    }
}
