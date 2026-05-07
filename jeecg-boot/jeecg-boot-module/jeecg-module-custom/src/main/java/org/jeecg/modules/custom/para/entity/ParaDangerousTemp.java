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
 * PARA_DANGEROUS_TEMP ParaDangerousTemp。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "PARA_DANGEROUS_TEMP")
@TableName("PARA_DANGEROUS_TEMP")
public class ParaDangerousTemp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "ID", width = 15)
    @Schema(description = "ID")
    private String id;

    /**
     * CODE_T_S；字段名: CODE_T_S；数据类型: VARCHAR2；长度: 16；可空: 是
     */
    @TableField("code_t_s")
    @Excel(name = "CODE_T_S", width = 15)
    @Schema(description = "CODE_T_S")
    private String codeTS;

    /**
     * G_NAME；字段名: G_NAME；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("g_name")
    @Excel(name = "G_NAME", width = 15)
    @Schema(description = "G_NAME")
    private String gName;

    /**
     * CIQ_NAME；字段名: CIQ_NAME；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("ciq_name")
    @Excel(name = "CIQ_NAME", width = 15)
    @Schema(description = "CIQ_NAME")
    private String ciqName;

    /**
     * RMK；字段名: RMK；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("rmk")
    @Excel(name = "RMK", width = 15)
    @Schema(description = "RMK")
    private String rmk;

    /**
     * ENABLED；字段名: ENABLED；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("enabled")
    @Excel(name = "ENABLED", width = 15)
    @Schema(description = "ENABLED")
    private Integer enabled;

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

    /**
     * CREATEUSERID；字段名: CREATEUSERID；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("createuserid")
    @Excel(name = "CREATEUSERID", width = 15)
    @Schema(description = "CREATEUSERID")
    private String createuserid;

    /**
     * CREATEBY；字段名: CREATEBY；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("createby")
    @Excel(name = "CREATEBY", width = 15)
    @Schema(description = "CREATEBY")
    private String createby;

    /**
     * MODIFIEDON；字段名: MODIFIEDON；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("modifiedon")
    @Excel(name = "MODIFIEDON", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "MODIFIEDON")
    private Date modifiedon;

    /**
     * MODIFIEDUSERID；字段名: MODIFIEDUSERID；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("modifieduserid")
    @Excel(name = "MODIFIEDUSERID", width = 15)
    @Schema(description = "MODIFIEDUSERID")
    private String modifieduserid;

    /**
     * MODIFIEDBY；字段名: MODIFIEDBY；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("modifiedby")
    @Excel(name = "MODIFIEDBY", width = 15)
    @Schema(description = "MODIFIEDBY")
    private String modifiedby;

}
