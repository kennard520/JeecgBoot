package org.jeecg.modules.custom.para.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.io.Serializable;

/**
 * 申报要素拉平格式 ParaMerchElement。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "申报要素拉平格式")
@TableName("PARA_MERCH_ELEMENT")
public class ParaMerchElement implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: CODE_TS；数据类型: VARCHAR2；长度: 10；可空: 否
     */
    @TableField("code_ts")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String codeTs;

    /**
     * 序号；字段名: S_NUM；数据类型: DECIMAL；长度: 22；精度: 9；可空: 否
     */
    @TableField("s_num")
    @Excel(name = "序号", width = 15)
    @Schema(description = "序号")
    private Integer sNum;

    /**
     * 规范要素；字段名: ELEMENT；数据类型: VARCHAR2；长度: 256；可空: 否
     */
    @TableField("element")
    @Excel(name = "规范要素", width = 15)
    @Schema(description = "规范要素")
    private String element;

    /**
     * 是否为空；字段名: ELEMENT_NULL；数据类型: VARCHAR2；长度: 1；可空: 是
     */
    @TableField("element_null")
    @Excel(name = "是否为空", width = 15)
    @Schema(description = "是否为空")
    private String elementNull;

    /**
     * 备注说明；字段名: ELEMENT_REMARK；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("element_remark")
    @Excel(name = "备注说明", width = 15)
    @Schema(description = "备注说明")
    private String elementRemark;

    /**
     * decfac_code；字段名: decfac_code；数据类型: VARCHAR2；长度: 255；可空: 是
     */
    @TableField("decfac_code")
    @Excel(name = "decfac_code", width = 15)
    @Schema(description = "decfac_code")
    private String decfacCode;

    /**
     * decfac_content；字段名: decfac_content；数据类型: VARCHAR2；长度: 255；可空: 是
     */
    @TableField("decfac_content")
    @Excel(name = "decfac_content", width = 15)
    @Schema(description = "decfac_content")
    private String decfacContent;

    /**
     * ie_flag；字段名: ie_flag；数据类型: VARCHAR2；长度: 10；可空: 是
     */
    @TableField("ie_flag")
    @Excel(name = "ie_flag", width = 15)
    @Schema(description = "ie_flag")
    private String ieFlag;

    /**
     * decfac_type；字段名: decfac_type；数据类型: VARCHAR2；长度: 10；可空: 是
     */
    @TableField("decfac_type")
    @Excel(name = "decfac_type", width = 15)
    @Schema(description = "decfac_type")
    private String decfacType;

}
