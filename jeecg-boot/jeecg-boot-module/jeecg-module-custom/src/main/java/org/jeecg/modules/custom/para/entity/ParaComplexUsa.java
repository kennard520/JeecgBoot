package org.jeecg.modules.custom.para.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 青岛海关对美加征税率表 ParaComplexUsa。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "青岛海关对美加征税率表")
@TableName("PARA_COMPLEX_USA")
public class ParaComplexUsa implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品编码；字段名: CODE_T_S；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "code_t_s", type = IdType.INPUT)
    @Excel(name = "商品编码", width = 15)
    @Schema(description = "商品编码")
    private String codeTS;

    /**
     * 中文品名；字段名: G_NAME；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("g_name")
    @Excel(name = "中文品名", width = 15)
    @Schema(description = "中文品名")
    private String gName;

    /**
     * 生效日期；字段名: BEGIN_DATE；数据类型: DATE；长度: 3；可空: 是
     */
    @TableField("begin_date")
    @Excel(name = "生效日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "生效日期")
    private Date beginDate;

    /**
     * 截止日期；字段名: END_DATE；数据类型: DATE；长度: 3；可空: 是
     */
    @TableField("end_date")
    @Excel(name = "截止日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "截止日期")
    private Date endDate;

    /**
     * 市场化采购排除后关税税率；字段名: DUTY_RATE；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("duty_rate")
    @Excel(name = "市场化采购排除后关税税率", width = 15)
    @Schema(description = "市场化采购排除后关税税率")
    private String dutyRate;

    /**
     * 对美加征后关税税率；字段名: DUTY_RATE_USA；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("duty_rate_usa")
    @Excel(name = "对美加征后关税税率", width = 15)
    @Schema(description = "对美加征后关税税率")
    private String dutyRateUsa;

    /**
     * 抓取日期；字段名: INPUT_DATE；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("input_date")
    @Excel(name = "抓取日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "抓取日期")
    private Date inputDate;

}
