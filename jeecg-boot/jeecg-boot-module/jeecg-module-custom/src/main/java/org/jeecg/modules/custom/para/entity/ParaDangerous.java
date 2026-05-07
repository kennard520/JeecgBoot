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
 * 危险化学品目录 ParaDangerous。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "危险化学品目录")
@TableName("PARA_DANGEROUS")
public class ParaDangerous implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 商品编号；字段名: CODE_T_S；数据类型: VARCHAR2；长度: 16；可空: 是
     */
    @TableField("code_t_s")
    @Excel(name = "商品编号", width = 15)
    @Schema(description = "商品编号")
    private String codeTS;

    /**
     * HS编码名称；字段名: G_NAME；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("g_name")
    @Excel(name = "HS编码名称", width = 15)
    @Schema(description = "HS编码名称")
    private String gName;

    /**
     * 检验检疫名称；字段名: CIQ_NAME；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("ciq_name")
    @Excel(name = "检验检疫名称", width = 15)
    @Schema(description = "检验检疫名称")
    private String ciqName;

    /**
     * 备注；字段名: RMK；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("rmk")
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String rmk;

    /**
     * 有效标识；字段名: ENABLED；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("enabled")
    @Excel(name = "有效标识", width = 15)
    @Schema(description = "有效标识")
    private Integer enabled;

    /**
     * 删除标志；字段名: DELETEMARK；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("deletemark")
    @Excel(name = "删除标志", width = 15)
    @Schema(description = "删除标志")
    private Integer deletemark;

    /**
     * 创建日期；字段名: CREATEON；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("createon")
    @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createon;

    /**
     * 创建用户主键；字段名: CREATEUSERID；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("createuserid")
    @Excel(name = "创建用户主键", width = 15)
    @Schema(description = "创建用户主键")
    private String createuserid;

    /**
     * 创建用户；字段名: CREATEBY；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("createby")
    @Excel(name = "创建用户", width = 15)
    @Schema(description = "创建用户")
    private String createby;

    /**
     * 修改日期；字段名: MODIFIEDON；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("modifiedon")
    @Excel(name = "修改日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改日期")
    private Date modifiedon;

    /**
     * 修改用户主键；字段名: MODIFIEDUSERID；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("modifieduserid")
    @Excel(name = "修改用户主键", width = 15)
    @Schema(description = "修改用户主键")
    private String modifieduserid;

    /**
     * 修改用户；字段名: MODIFIEDBY；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("modifiedby")
    @Excel(name = "修改用户", width = 15)
    @Schema(description = "修改用户")
    private String modifiedby;

}
