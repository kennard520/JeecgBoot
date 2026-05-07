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
 * PARA_COMPLEX_CIQ_SINGLE ParaComplexCiqSingle。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "PARA_COMPLEX_CIQ_SINGLE")
@TableName("PARA_COMPLEX_CIQ_SINGLE")
public class ParaComplexCiqSingle implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * CIQNAME；字段名: CIQNAME；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("ciqname")
    @Excel(name = "CIQNAME", width = 15)
    @Schema(description = "CIQNAME")
    private String ciqname;

    /**
     * CLASSIFYCNNM；字段名: CLASSIFYCNNM；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("classifycnnm")
    @Excel(name = "CLASSIFYCNNM", width = 15)
    @Schema(description = "CLASSIFYCNNM")
    private String classifycnnm;

    /**
     * HSCODE；字段名: HSCODE；数据类型: VARCHAR2；长度: 50；可空: 否
     */
    @TableField("hscode")
    @Excel(name = "HSCODE", width = 15)
    @Schema(description = "HSCODE")
    private String hscode;

    /**
     * HSNAME；字段名: HSNAME；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("hsname")
    @Excel(name = "HSNAME", width = 15)
    @Schema(description = "HSNAME")
    private String hsname;

    /**
     * STATCODE；字段名: STATCODE；数据类型: VARCHAR2；长度: 50；可空: 否
     */
    @TableId(value = "statcode", type = IdType.INPUT)
    @Excel(name = "STATCODE", width = 15)
    @Schema(description = "STATCODE")
    private String statcode;

    /**
     * INPUT_DATE；字段名: INPUT_DATE；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("input_date")
    @Excel(name = "INPUT_DATE", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "INPUT_DATE")
    private Date inputDate;

}
