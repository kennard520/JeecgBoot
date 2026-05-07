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
import org.jeecg.modules.custom.para.entity.ParaTradeTerm;
import org.jeecg.modules.custom.para.service.IParaTradeTermService;
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
 * 贸易条款 ParaTradeTerm 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "贸易条款 ParaTradeTerm")
@RestController
@RequestMapping("/custom/para/paraTradeTerm")
public class ParaTradeTermController extends JeecgController<ParaTradeTerm, IParaTradeTermService> {
    @Autowired
    private IParaTradeTermService paraTradeTermService;

    /**
     * 分页查询 贸易条款。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询贸易条款")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraTradeTerm")
    public Result<IPage<ParaTradeTerm>> queryPageList(ParaTradeTerm paraTradeTerm,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaTradeTerm> queryWrapper = QueryGenerator.initQueryWrapper(paraTradeTerm, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaTradeTerm> page = new Page<>(pageNo, pageSize);
        IPage<ParaTradeTerm> pageList = paraTradeTermService.page(page, queryWrapper);
        log.debug("分页查询贸易条款，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 贸易条款。
     */
    @AutoLog(value = "新增贸易条款")
    @Operation(summary = "新增贸易条款")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaTradeTerm paraTradeTerm) {
        paraTradeTermService.save(paraTradeTerm);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 贸易条款。
     */
    @AutoLog(value = "编辑贸易条款", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑贸易条款")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaTradeTerm paraTradeTerm) {
        paraTradeTermService.updateById(paraTradeTerm);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 贸易条款。
     */
    @AutoLog(value = "删除贸易条款")
    @Operation(summary = "通过ID删除贸易条款")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraTradeTermService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 贸易条款。
     */
    @AutoLog(value = "批量删除贸易条款")
    @Operation(summary = "批量删除贸易条款")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraTradeTermService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 贸易条款 明细。
     */
    @Operation(summary = "通过ID查询贸易条款")
    @GetMapping(value = "/queryById")
    public Result<ParaTradeTerm> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaTradeTerm paraTradeTerm = paraTradeTermService.getById(id);
        return Result.OK(paraTradeTerm);
    }

    /**
     * 导出 贸易条款 Excel。
     */
    @Operation(summary = "导出贸易条款Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaTradeTerm paraTradeTerm) {
        return super.exportXls(request, paraTradeTerm, ParaTradeTerm.class, "贸易条款");
    }

    /**
     * 从 Excel 导入 贸易条款。
     */
    @Operation(summary = "从Excel导入贸易条款")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaTradeTerm.class);
    }
}
