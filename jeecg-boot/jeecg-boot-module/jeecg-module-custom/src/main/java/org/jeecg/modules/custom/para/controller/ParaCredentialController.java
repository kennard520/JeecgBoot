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
import org.jeecg.modules.custom.para.entity.ParaCredential;
import org.jeecg.modules.custom.para.service.IParaCredentialService;
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
 * 申请单证参数 ParaCredential 控制器。
 *
 * <p>提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "申请单证参数 ParaCredential")
@RestController
@RequestMapping("/custom/para/paraCredential")
public class ParaCredentialController extends JeecgController<ParaCredential, IParaCredentialService> {
    @Autowired
    private IParaCredentialService paraCredentialService;

    /**
     * 分页查询申请单证参数。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按申请单证代码升序返回。</p>
     */
    @Operation(summary = "分页查询申请单证参数")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/para/paraCredential")
    public Result<IPage<ParaCredential>> queryPageList(ParaCredential paraCredential,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        QueryWrapper<ParaCredential> queryWrapper = QueryGenerator.initQueryWrapper(paraCredential, req.getParameterMap());
        queryWrapper.orderByAsc("CODE");
        Page<ParaCredential> page = new Page<>(pageNo, pageSize);
        IPage<ParaCredential> pageList = paraCredentialService.page(page, queryWrapper);
        log.debug("分页查询申请单证参数，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增申请单证参数。
     */
    @AutoLog(value = "新增申请单证参数")
    @Operation(summary = "新增申请单证参数")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ParaCredential paraCredential) {
        paraCredentialService.save(paraCredential);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑申请单证参数。
     */
    @AutoLog(value = "编辑申请单证参数", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑申请单证参数")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ParaCredential paraCredential) {
        paraCredentialService.updateById(paraCredential);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除申请单证参数。
     */
    @AutoLog(value = "删除申请单证参数")
    @Operation(summary = "通过ID删除申请单证参数")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        paraCredentialService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除申请单证参数。
     */
    @AutoLog(value = "批量删除申请单证参数")
    @Operation(summary = "批量删除申请单证参数")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        paraCredentialService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询申请单证参数明细。
     */
    @Operation(summary = "通过ID查询申请单证参数")
    @GetMapping(value = "/queryById")
    public Result<ParaCredential> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                            @RequestParam(name = "id", required = true) String id) {
        ParaCredential paraCredential = paraCredentialService.getById(id);
        return Result.OK(paraCredential);
    }

    /**
     * 导出申请单证参数 Excel。
     */
    @Operation(summary = "导出申请单证参数Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ParaCredential paraCredential) {
        return super.exportXls(request, paraCredential, ParaCredential.class, "申请单证参数");
    }

    /**
     * 从 Excel 导入申请单证参数。
     */
    @Operation(summary = "从Excel导入申请单证参数")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ParaCredential.class);
    }
}
