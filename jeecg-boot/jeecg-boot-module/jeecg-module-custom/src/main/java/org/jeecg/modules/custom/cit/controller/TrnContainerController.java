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
import org.jeecg.modules.custom.cit.entity.TrnContainer;
import org.jeecg.modules.custom.cit.service.ITrnContainerService;
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
 * 进口/出口提运单集装箱表 TrnContainer 控制器。
 *
 * <p>根据 CIT.sql 自动生成基础管理接口，接口只处理通用 CRUD、Excel 导入导出和分页查询；字段级含义请查看实体类注释。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Slf4j
@Tag(name = "进口/出口提运单集装箱表 TrnContainer")
@RestController
@RequestMapping("/custom/cit/trnContainer")
public class TrnContainerController extends JeecgController<TrnContainer, ITrnContainerService> {
    @Autowired
    private ITrnContainerService trnContainerService;

    /**
     * 分页查询 进口/出口提运单集装箱表 TrnContainer。
     *
     * <p>查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按主键 ID 倒序返回。</p>
     *
     * @param trnContainer 查询条件实体，字段为空时不参与过滤
     * @param pageNo 当前页码，默认第 1 页
     * @param pageSize 每页数量，默认 10 条
     * @param req HTTP 请求，用于读取动态查询参数
     * @return 分页后的 进口/出口提运单集装箱表 TrnContainer 数据
     */
    @Operation(summary = "分页查询进口/出口提运单集装箱表 TrnContainer")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "custom/cit/trnContainer")
    public Result<IPage<TrnContainer>> queryPageList(TrnContainer trnContainer,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<TrnContainer> queryWrapper = QueryGenerator.initQueryWrapper(trnContainer, req.getParameterMap());
        queryWrapper.orderByDesc("ID");
        Page<TrnContainer> page = new Page<>(pageNo, pageSize);
        IPage<TrnContainer> pageList = trnContainerService.page(page, queryWrapper);
        log.debug("分页查询进口/出口提运单集装箱表 TrnContainer，当前页={}，每页数量={}，总数={}", pageNo, pageSize, pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 新增 进口/出口提运单集装箱表 TrnContainer。
     *
     * @param trnContainer 待保存的数据对象，字段定义和必填说明见实体类注释
     * @return 操作结果
     */
    @AutoLog(value = "新增进口/出口提运单集装箱表 TrnContainer")
    @Operation(summary = "新增进口/出口提运单集装箱表 TrnContainer")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody TrnContainer trnContainer) {
        trnContainerService.save(trnContainer);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑 进口/出口提运单集装箱表 TrnContainer。
     *
     * @param trnContainer 带主键 ID 的数据对象，仅更新实体中提交的字段
     * @return 操作结果
     */
    @AutoLog(value = "编辑进口/出口提运单集装箱表 TrnContainer", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "编辑进口/出口提运单集装箱表 TrnContainer")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody TrnContainer trnContainer) {
        trnContainerService.updateById(trnContainer);
        return Result.OK("更新成功！");
    }

    /**
     * 通过 ID 删除 进口/出口提运单集装箱表 TrnContainer。
     *
     * @param id 主键 ID
     * @return 操作结果
     */
    @AutoLog(value = "删除进口/出口提运单集装箱表 TrnContainer")
    @Operation(summary = "通过ID删除进口/出口提运单集装箱表 TrnContainer")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        trnContainerService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除 进口/出口提运单集装箱表 TrnContainer。
     *
     * @param ids 英文逗号分隔的主键 ID 列表
     * @return 操作结果
     */
    @AutoLog(value = "批量删除进口/出口提运单集装箱表 TrnContainer")
    @Operation(summary = "批量删除进口/出口提运单集装箱表 TrnContainer")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        trnContainerService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过 ID 查询 进口/出口提运单集装箱表 TrnContainer 明细。
     *
     * @param id 主键 ID
     * @return 匹配的 进口/出口提运单集装箱表 TrnContainer 数据；不存在时返回空结果
     */
    @Operation(summary = "通过ID查询进口/出口提运单集装箱表 TrnContainer")
    @GetMapping(value = "/queryById")
    public Result<TrnContainer> queryById(@Parameter(name = "id", description = "主键ID", required = true)
                                          @RequestParam(name = "id", required = true) String id) {
        TrnContainer trnContainer = trnContainerService.getById(id);
        return Result.OK(trnContainer);
    }

    /**
     * 导出 进口/出口提运单集装箱表 TrnContainer Excel。
     *
     * @param request HTTP 请求，支持携带查询条件和 selections 过滤导出范围
     * @param trnContainer 查询条件实体
     * @return Excel 视图
     */
    @Operation(summary = "导出进口/出口提运单集装箱表 TrnContainerExcel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TrnContainer trnContainer) {
        return super.exportXls(request, trnContainer, TrnContainer.class, "进口/出口提运单集装箱表 TrnContainer");
    }

    /**
     * 从 Excel 导入 进口/出口提运单集装箱表 TrnContainer。
     *
     * @param request HTTP 请求，包含上传文件
     * @param response HTTP 响应
     * @return 导入结果
     */
    @Operation(summary = "从Excel导入进口/出口提运单集装箱表 TrnContainer")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TrnContainer.class);
    }
}
