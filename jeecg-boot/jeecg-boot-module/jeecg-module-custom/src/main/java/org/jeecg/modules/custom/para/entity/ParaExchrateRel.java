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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 海关汇率 ParaExchrateRel。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "海关汇率")
@TableName("PARA_EXCHRATE_REL")
public class ParaExchrateRel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 256；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 起始日期；字段名: BEGIN_DATE；数据类型: DATE；长度: 3；可空: 否
     */
    @TableField("begin_date")
    @Excel(name = "起始日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "起始日期")
    private Date beginDate;

    /**
     * 结束日期；字段名: END_DATE；数据类型: DATE；长度: 3；可空: 否
     */
    @TableField("end_date")
    @Excel(name = "结束日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束日期")
    private Date endDate;

    /**
     * 币别编码；字段名: CURR_CODE；数据类型: VARCHAR2；长度: 32；可空: 否
     */
    @TableField("curr_code")
    @Excel(name = "币别编码", width = 15)
    @Schema(description = "币别编码")
    private String currCode;

    /**
     * 转换率；字段名: T_RATE；数据类型: DECIMAL；长度: 22；精度: 18；小数位: 8；可空: 是
     */
    @TableField("t_rate")
    @Excel(name = "转换率", width = 15)
    @Schema(description = "转换率")
    private BigDecimal tRate;

    /**
     * 对人民币汇率；字段名: RMB_RATE；数据类型: DECIMAL；长度: 22；精度: 18；小数位: 8；可空: 是
     */
    @TableField("rmb_rate")
    @Excel(name = "对人民币汇率", width = 15)
    @Schema(description = "对人民币汇率")
    private BigDecimal rmbRate;

    /**
     * 对美元汇率；字段名: USD_RATE；数据类型: DECIMAL；长度: 22；精度: 18；小数位: 8；可空: 是
     */
    @TableField("usd_rate")
    @Excel(name = "对美元汇率", width = 15)
    @Schema(description = "对美元汇率")
    private BigDecimal usdRate;

    /**
     * 今日汇率；字段名: TODAY_RATE；数据类型: DECIMAL；长度: 22；精度: 18；小数位: 8；可空: 是
     */
    @TableField("today_rate")
    @Excel(name = "今日汇率", width = 15)
    @Schema(description = "今日汇率")
    private BigDecimal todayRate;

    /**
     * 序号；字段名: PK_SEQ；数据类型: DECIMAL；长度: 22；精度: 8；可空: 是
     */
    @TableField("pk_seq")
    @Excel(name = "序号", width = 15)
    @Schema(description = "序号")
    private Integer pkSeq;

}
