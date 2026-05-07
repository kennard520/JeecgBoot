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
 * PARA_EXCHRATE_REL_IMPORT_TEMP ParaExchrateRelImportTemp。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "PARA_EXCHRATE_REL_IMPORT_TEMP")
@TableName("PARA_EXCHRATE_REL_IMPORT_TEMP")
public class ParaExchrateRelImportTemp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "ID", width = 15)
    @Schema(description = "ID")
    private String id;

    /**
     * BEGIN_DATE；字段名: BEGIN_DATE；数据类型: DATE；长度: 3；可空: 否
     */
    @TableField("begin_date")
    @Excel(name = "BEGIN_DATE", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "BEGIN_DATE")
    private Date beginDate;

    /**
     * END_DATE；字段名: END_DATE；数据类型: DATE；长度: 3；可空: 否
     */
    @TableField("end_date")
    @Excel(name = "END_DATE", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "END_DATE")
    private Date endDate;

    /**
     * CURR_CODE；字段名: CURR_CODE；数据类型: VARCHAR2；长度: 8；可空: 否
     */
    @TableField("curr_code")
    @Excel(name = "CURR_CODE", width = 15)
    @Schema(description = "CURR_CODE")
    private String currCode;

    /**
     * RMB_RATE；字段名: RMB_RATE；数据类型: DECIMAL；长度: 22；精度: 18；小数位: 8；可空: 是
     */
    @TableField("rmb_rate")
    @Excel(name = "RMB_RATE", width = 15)
    @Schema(description = "RMB_RATE")
    private BigDecimal rmbRate;

    /**
     * USD_RATE；字段名: USD_RATE；数据类型: DECIMAL；长度: 22；精度: 18；小数位: 8；可空: 是
     */
    @TableField("usd_rate")
    @Excel(name = "USD_RATE", width = 15)
    @Schema(description = "USD_RATE")
    private BigDecimal usdRate;

}
