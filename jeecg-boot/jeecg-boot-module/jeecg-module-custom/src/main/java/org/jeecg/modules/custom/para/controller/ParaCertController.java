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
import org.jeecg.modules.custom.para.entity.ParaCert;
import org.jeecg.modules.custom.para.service.IParaCertService;
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
 * PARA_CERT ParaCert 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "PARA_CERT ParaCert")
@RestController
@RequestMapping("/custom/para/paraCert")
public class ParaCertController extends JeecgController<ParaCert, IParaCertService> {
    @Autowired
    private IParaCertService paraCertService;

    /**
     * 分页查询 PARA_CERT。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询PARA_CERT")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraCert")
    public Result<IPage<ParaCert>> queryPageList(ParaCert paraCert,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaCert> queryWrapper = QueryGenerator.initQueryWrapper(paraCert, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaCert> page = new Page<>(pageNo, pageSize);
        IPage<ParaCert> pageList = paraCertService.page(page, queryWrapper);
        log.debug("分页查询PARA_CERT，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 PARA_CERT。
     */
    @AutoLog(value = "新增PARA_CERT")
    @Operation(summary = "新增PARA_CERT")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaCert paraCert) {
        paraCertService.save(paraCert);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 PARA_CERT。
     */
    @AutoLog(value = "编辑PARA_CERT", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑PARA_CERT")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaCert paraCert) {
        paraCertService.updateById(paraCert);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 PARA_CERT。
     */
    @AutoLog(value = "删除PARA_CERT")
    @Operation(summary = "通过ID删除PARA_CERT")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraCertService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 PARA_CERT。
     */
    @AutoLog(value = "批量删除PARA_CERT")
    @Operation(summary = "批量删除PARA_CERT")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraCertService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 PARA_CERT 明细。
     */
    @Operation(summary = "通过ID查询PARA_CERT")
    @GetMapping(value = "/queryById")
    public Result<ParaCert> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaCert paraCert = paraCertService.getById(id);
        return Result.OK(paraCert);
    }

    /**
     * 导出 PARA_CERT Excel。
     */
    @Operation(summary = "导出PARA_CERTExcel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaCert paraCert) {
        return super.exportXls(request, paraCert, ParaCert.class, "PARA_CERT");
    }

    /**
     * 从 Excel 导入 PARA_CERT。
     */
    @Operation(summary = "从Excel导入PARA_CERT")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaCert.class);
    }
}
