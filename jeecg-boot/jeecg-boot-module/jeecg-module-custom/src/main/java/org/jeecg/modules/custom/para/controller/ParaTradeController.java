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
import org.jeecg.modules.custom.para.entity.ParaTrade;
import org.jeecg.modules.custom.para.service.IParaTradeService;
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
 * 贸易方式(监管方式) ParaTrade 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "贸易方式(监管方式) ParaTrade")
@RestController
@RequestMapping("/custom/para/paraTrade")
public class ParaTradeController extends JeecgController<ParaTrade, IParaTradeService> {
    @Autowired
    private IParaTradeService paraTradeService;

    /**
     * 分页查询 贸易方式(监管方式)。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询贸易方式(监管方式)")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraTrade")
    public Result<IPage<ParaTrade>> queryPageList(ParaTrade paraTrade,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaTrade> queryWrapper = QueryGenerator.initQueryWrapper(paraTrade, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaTrade> page = new Page<>(pageNo, pageSize);
        IPage<ParaTrade> pageList = paraTradeService.page(page, queryWrapper);
        log.debug("分页查询贸易方式(监管方式)，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 贸易方式(监管方式)。
     */
    @AutoLog(value = "新增贸易方式(监管方式)")
    @Operation(summary = "新增贸易方式(监管方式)")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaTrade paraTrade) {
        paraTradeService.save(paraTrade);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 贸易方式(监管方式)。
     */
    @AutoLog(value = "编辑贸易方式(监管方式)", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑贸易方式(监管方式)")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaTrade paraTrade) {
        paraTradeService.updateById(paraTrade);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 贸易方式(监管方式)。
     */
    @AutoLog(value = "删除贸易方式(监管方式)")
    @Operation(summary = "通过ID删除贸易方式(监管方式)")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraTradeService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 贸易方式(监管方式)。
     */
    @AutoLog(value = "批量删除贸易方式(监管方式)")
    @Operation(summary = "批量删除贸易方式(监管方式)")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraTradeService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 贸易方式(监管方式) 明细。
     */
    @Operation(summary = "通过ID查询贸易方式(监管方式)")
    @GetMapping(value = "/queryById")
    public Result<ParaTrade> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaTrade paraTrade = paraTradeService.getById(id);
        return Result.OK(paraTrade);
    }

    /**
     * 导出 贸易方式(监管方式) Excel。
     */
    @Operation(summary = "导出贸易方式(监管方式)Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaTrade paraTrade) {
        return super.exportXls(request, paraTrade, ParaTrade.class, "贸易方式(监管方式)");
    }

    /**
     * 从 Excel 导入 贸易方式(监管方式)。
     */
    @Operation(summary = "从Excel导入贸易方式(监管方式)")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaTrade.class);
    }
}
