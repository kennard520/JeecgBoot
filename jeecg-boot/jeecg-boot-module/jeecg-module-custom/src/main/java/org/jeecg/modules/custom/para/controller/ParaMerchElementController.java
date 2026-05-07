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
import org.jeecg.modules.custom.para.entity.ParaMerchElement;
import org.jeecg.modules.custom.para.service.IParaMerchElementService;
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
 * 申报要素拉平格式 ParaMerchElement 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "申报要素拉平格式 ParaMerchElement")
@RestController
@RequestMapping("/custom/para/paraMerchElement")
public class ParaMerchElementController extends JeecgController<ParaMerchElement, IParaMerchElementService> {
    @Autowired
    private IParaMerchElementService paraMerchElementService;

    /**
     * 分页查询 申报要素拉平格式。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 ID 倒序返回。</p>
     */
    @Operation(summary = "分页查询申报要素拉平格式")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraMerchElement")
    public Result<IPage<ParaMerchElement>> queryPageList(ParaMerchElement paraMerchElement,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<ParaMerchElement> queryWrapper = QueryGenerator.initQueryWrapper(paraMerchElement, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<ParaMerchElement> page = new Page<>(pageNo, pageSize);
        IPage<ParaMerchElement> pageList = paraMerchElementService.page(page, queryWrapper);
        log.debug("分页查询申报要素拉平格式，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 申报要素拉平格式。
     */
    @AutoLog(value = "新增申报要素拉平格式")
    @Operation(summary = "新增申报要素拉平格式")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaMerchElement paraMerchElement) {
        paraMerchElementService.save(paraMerchElement);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 申报要素拉平格式。
     */
    @AutoLog(value = "编辑申报要素拉平格式", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑申报要素拉平格式")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaMerchElement paraMerchElement) {
        paraMerchElementService.updateById(paraMerchElement);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 申报要素拉平格式。
     */
    @AutoLog(value = "删除申报要素拉平格式")
    @Operation(summary = "通过ID删除申报要素拉平格式")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraMerchElementService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 申报要素拉平格式。
     */
    @AutoLog(value = "批量删除申报要素拉平格式")
    @Operation(summary = "批量删除申报要素拉平格式")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraMerchElementService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 申报要素拉平格式 明细。
     */
    @Operation(summary = "通过ID查询申报要素拉平格式")
    @GetMapping(value = "/queryById")
    public Result<ParaMerchElement> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        ParaMerchElement paraMerchElement = paraMerchElementService.getById(id);
        return Result.OK(paraMerchElement);
    }

    /**
     * 导出 申报要素拉平格式 Excel。
     */
    @Operation(summary = "导出申报要素拉平格式Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaMerchElement paraMerchElement) {
        return super.exportXls(request, paraMerchElement, ParaMerchElement.class, "申报要素拉平格式");
    }

    /**
     * 从 Excel 导入 申报要素拉平格式。
     */
    @Operation(summary = "从Excel导入申报要素拉平格式")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaMerchElement.class);
    }
}
