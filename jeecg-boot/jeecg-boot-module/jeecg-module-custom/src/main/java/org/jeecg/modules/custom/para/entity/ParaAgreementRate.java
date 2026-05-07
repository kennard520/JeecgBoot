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
 * PARA_AGREEMENT_RATE ParaAgreementRate。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "PARA_AGREEMENT_RATE")
@TableName("PARA_AGREEMENT_RATE")
public class ParaAgreementRate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "ID", width = 15)
    @Schema(description = "ID")
    private String id;

    /**
     * CODE_T_S；字段名: CODE_T_S；数据类型: VARCHAR2；长度: 16；可空: 否
     */
    @TableField("code_t_s")
    @Excel(name = "CODE_T_S", width = 15)
    @Schema(description = "CODE_T_S")
    private String codeTS;

    /**
     * AGREEMENT_CODE；字段名: AGREEMENT_CODE；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableField("agreement_code")
    @Excel(name = "AGREEMENT_CODE", width = 15)
    @Schema(description = "AGREEMENT_CODE")
    private String agreementCode;

    /**
     * BEGIN_DATE；字段名: BEGIN_DATE；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("begin_date")
    @Excel(name = "BEGIN_DATE", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "BEGIN_DATE")
    private Date beginDate;

    /**
     * END_DATE；字段名: END_DATE；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("end_date")
    @Excel(name = "END_DATE", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "END_DATE")
    private Date endDate;

    /**
     * DUTY_TYPE；字段名: DUTY_TYPE；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("duty_type")
    @Excel(name = "DUTY_TYPE", width = 15)
    @Schema(description = "DUTY_TYPE")
    private String dutyType;

    /**
     * DUTY_RATE；字段名: DUTY_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("duty_rate")
    @Excel(name = "DUTY_RATE", width = 15)
    @Schema(description = "DUTY_RATE")
    private BigDecimal dutyRate;

    /**
     * NOTE；字段名: NOTE；数据类型: VARCHAR2；长度: 255；可空: 是
     */
    @TableField("note")
    @Excel(name = "NOTE", width = 15)
    @Schema(description = "NOTE")
    private String note;

    /**
     * DELETEMARK；字段名: DELETEMARK；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("deletemark")
    @Excel(name = "DELETEMARK", width = 15)
    @Schema(description = "DELETEMARK")
    private Integer deletemark;

    /**
     * CREATEON；字段名: CREATEON；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("createon")
    @Excel(name = "CREATEON", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "CREATEON")
    private Date createon;

}
