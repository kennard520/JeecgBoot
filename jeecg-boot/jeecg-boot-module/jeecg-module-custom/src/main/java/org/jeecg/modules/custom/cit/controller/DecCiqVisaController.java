package org.jeecg.modules.custom.cit.controller;

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
import org.jeecg.modules.custom.cit.entity.DecCiqVisa;
import org.jeecg.modules.custom.cit.service.IDecCiqVisaService;
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
 * 检验检疫签证申报要素 DecCiqVisa 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "检验检疫签证申报要素 DecCiqVisa")
@RestController
@RequestMapping("/custom/cit/decCiqVisa")
public class DecCiqVisaController extends JeecgController<DecCiqVisa, IDecCiqVisaService> {
    @Autowired
    private IDecCiqVisaService decCiqVisaService;

    /**
     * 分页查询检验检疫签证申报要素。
     */
    @Operation(summary = "分页查询检验检疫签证申报要素")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/cit/decCiqVisa")
    public Result<IPage<DecCiqVisa>> queryPageList(DecCiqVisa decCiqVisa,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<DecCiqVisa> queryWrapper = QueryGenerator.initQueryWrapper(decCiqVisa, req.getParameterMap());
        queryWrapper.orderByAsc("CERT_CODE").orderByDesc("ID");
        Page<DecCiqVisa> page = new Page<>(pageNo, pageSize);
        IPage<DecCiqVisa> pageList = decCiqVisaService.page(page, queryWrapper);
        log.debug("分页查询检验检疫签证申报要素，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增检验检疫签证申报要素。
     */
    @AutoLog(value = "新增检验检疫签证申报要素")
    @Operation(summary = "新增检验检疫签证申报要素")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DecCiqVisa decCiqVisa) {
        decCiqVisaService.save(decCiqVisa);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑检验检疫签证申报要素。
     */
    @AutoLog(value = "编辑检验检疫签证申报要素", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑检验检疫签证申报要素")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody DecCiqVisa decCiqVisa) {
        decCiqVisaService.updateById(decCiqVisa);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除检验检疫签证申报要素。
     */
    @AutoLog(value = "删除检验检疫签证申报要素")
    @Operation(summary = "通过ID删除检验检疫签证申报要素")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        decCiqVisaService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除检验检疫签证申报要素。
     */
    @AutoLog(value = "批量删除检验检疫签证申报要素")
    @Operation(summary = "批量删除检验检疫签证申报要素")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        decCiqVisaService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询检验检疫签证申报要素明细。
     */
    @Operation(summary = "通过ID查询检验检疫签证申报要素")
    @GetMapping(value = "/queryById")
    public Result<DecCiqVisa> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                        @RequestParam(name = "id", required = true) String id) {
        DecCiqVisa decCiqVisa = decCiqVisaService.getById(id);
        return Result.OK(decCiqVisa);
    }

    /**
     * 导出检验检疫签证申报要素 Excel。
     */
    @Operation(summary = "导出检验检疫签证申报要素Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DecCiqVisa decCiqVisa) {
        return super.exportXls(request, decCiqVisa, DecCiqVisa.class, "检验检疫签证申报要素");
    }

    /**
     * 从 Excel 导入检验检疫签证申报要素。
     */
    @Operation(summary = "从Excel导入检验检疫签证申报要素")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, DecCiqVisa.class);
    }
}
