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
import org.jeecg.modules.custom.cit.entity.EdocRelation;
import org.jeecg.modules.custom.cit.service.IEdocRelationService;
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
 * 电子随附单据关联关系信息EdocRealation 控制器。
 *
 * <p>根据 CIT.sql 自动生成基础管理接口，接口只处理通用 CRUD、Excel 导入导出和分页查询；字段级含义请查看实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "电子随附单据关联关系信息EdocRealation")
@RestController
@RequestMapping("/custom/cit/edocRelation")
public class EdocRelationController extends JeecgController<EdocRelation, IEdocRelationService> {
    @Autowired
    private IEdocRelationService edocRelationService;

    /**
     * 分页查询 电子随附单据关联关系信息EdocRealation。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按主键 ID 倒序返回。</p>
     *
     * @param edocRelation 查询条件实体，字段为空时不参与过滤
     * @param pageNo 当前页码，默认第 1 页
     * @param pageSize 每页数量，默认 10 条
     * @param req HTTP 请求，用于读取动态查询参数
     * @return 分页后的 电子随附单据关联关系信息EdocRealation 数据
     */
    @Operation(summary = "分页查询电子随附单据关联关系信息EdocRealation")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/cit/edocRelation")
    public Result<IPage<EdocRelation>> queryPageList(EdocRelation edocRelation,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<EdocRelation> queryWrapper = QueryGenerator.initQueryWrapper(edocRelation, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<EdocRelation> page = new Page<>(pageNo, pageSize);
        IPage<EdocRelation> pageList = edocRelationService.page(page, queryWrapper);
        log.debug("分页查询电子随附单据关联关系信息EdocRealation，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 电子随附单据关联关系信息EdocRealation。
     *
     * @param edocRelation 待保存的数据对象，字段定义和必填说明见实体类注释
     * @return 操作结果
     */
    @AutoLog(value = "新增电子随附单据关联关系信息EdocRealation")
    @Operation(summary = "新增电子随附单据关联关系信息EdocRealation")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody EdocRelation edocRelation) {
        edocRelationService.save(edocRelation);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 电子随附单据关联关系信息EdocRealation。
     *
     * @param edocRelation 带主键 ID 的数据对象，仅更新实体中提交的字段
     * @return 操作结果
     */
    @AutoLog(value = "编辑电子随附单据关联关系信息EdocRealation", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑电子随附单据关联关系信息EdocRealation")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EdocRelation edocRelation) {
        edocRelationService.updateById(edocRelation);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 电子随附单据关联关系信息EdocRealation。
     *
     * @param id 主键 ID
     * @return 操作结果
     */
    @AutoLog(value = "删除电子随附单据关联关系信息EdocRealation")
    @Operation(summary = "通过ID删除电子随附单据关联关系信息EdocRealation")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        edocRelationService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 电子随附单据关联关系信息EdocRealation。
     *
     * @param ids 英文逗号分隔的主键 ID 列表
     * @return 操作结果
     */
    @AutoLog(value = "批量删除电子随附单据关联关系信息EdocRealation")
    @Operation(summary = "批量删除电子随附单据关联关系信息EdocRealation")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        edocRelationService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 电子随附单据关联关系信息EdocRealation 明细。
     *
     * @param id 主键 ID
     * @return 匹配的 电子随附单据关联关系信息EdocRealation 数据；不存在时返回空结果
     */
    @Operation(summary = "通过ID查询电子随附单据关联关系信息EdocRealation")
    @GetMapping(value = "/queryById")
    public Result<EdocRelation> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        EdocRelation edocRelation = edocRelationService.getById(id);
        return Result.OK(edocRelation);
    }

    /**
     * 导出 电子随附单据关联关系信息EdocRealation Excel。
     *
     * @param request HTTP 请求，支持携带查询条件和 selections 过滤导出范围
     * @param edocRelation 查询条件实体
     * @return Excel 视图
     */
    @Operation(summary = "导出电子随附单据关联关系信息EdocRealationExcel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EdocRelation edocRelation) {
        return super.exportXls(request, edocRelation, EdocRelation.class, "电子随附单据关联关系信息EdocRealation");
    }

    /**
     * 从 Excel 导入 电子随附单据关联关系信息EdocRealation。
     *
     * @param request HTTP 请求，包含上传文件
     * @param response HTTP 响应
     * @return 导入结果
     */
    @Operation(summary = "从Excel导入电子随附单据关联关系信息EdocRealation")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EdocRelation.class);
    }
}
