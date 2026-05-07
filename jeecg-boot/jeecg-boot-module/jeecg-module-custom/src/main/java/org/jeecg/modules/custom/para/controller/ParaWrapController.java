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
import org.jeecg.modules.custom.para.entity.ParaWrap;
import org.jeecg.modules.custom.para.service.IParaWrapService;
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
 * 包装方式 ParaWrap 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "包装方式 ParaWrap")
@RestController
@RequestMapping("/custom/para/paraWrap")
public class ParaWrapController extends JeecgController<ParaWrap, IParaWrapService> {
    @Autowired
    private IParaWrapService paraWrapService;

    /**
     * 分页查询 包装方式。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询包装方式")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraWrap")
    public Result<IPage<ParaWrap>> queryPageList(ParaWrap paraWrap,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaWrap> queryWrapper = QueryGenerator.initQueryWrapper(paraWrap, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaWrap> page = new Page<>(pageNo, pageSize);
        IPage<ParaWrap> pageList = paraWrapService.page(page, queryWrapper);
        log.debug("分页查询包装方式，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 包装方式。
     */
    @AutoLog(value = "新增包装方式")
    @Operation(summary = "新增包装方式")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaWrap paraWrap) {
        paraWrapService.save(paraWrap);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 包装方式。
     */
    @AutoLog(value = "编辑包装方式", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑包装方式")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaWrap paraWrap) {
        paraWrapService.updateById(paraWrap);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 包装方式。
     */
    @AutoLog(value = "删除包装方式")
    @Operation(summary = "通过ID删除包装方式")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraWrapService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 包装方式。
     */
    @AutoLog(value = "批量删除包装方式")
    @Operation(summary = "批量删除包装方式")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraWrapService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 包装方式 明细。
     */
    @Operation(summary = "通过ID查询包装方式")
    @GetMapping(value = "/queryById")
    public Result<ParaWrap> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaWrap paraWrap = paraWrapService.getById(id);
        return Result.OK(paraWrap);
    }

    /**
     * 导出 包装方式 Excel。
     */
    @Operation(summary = "导出包装方式Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaWrap paraWrap) {
        return super.exportXls(request, paraWrap, ParaWrap.class, "包装方式");
    }

    /**
     * 从 Excel 导入 包装方式。
     */
    @Operation(summary = "从Excel导入包装方式")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaWrap.class);
    }
}
