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
 * 币制 ParaCurr。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "币制")
@TableName("PARA_CURR")
public class ParaCurr implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: CURR_CODE；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("curr_code")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String currCode;

    /**
     * 英文简称；字段名: CURR_SYMB；数据类型: VARCHAR2；长度: 8；可空: 是
     */
    @TableField("curr_symb")
    @Excel(name = "英文简称", width = 15)
    @Schema(description = "英文简称")
    private String currSymb;

    /**
     * 名称；字段名: CURR_NAME；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("curr_name")
    @Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String currName;

    /**
     * 公司名称；字段名: COMPANY_CURR_NAME；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("company_curr_name")
    @Excel(name = "公司名称", width = 15)
    @Schema(description = "公司名称")
    private String companyCurrName;

    /**
     * 公司代码；字段名: COMPANY_CURR_CODE；数据类型: VARCHAR2；长度: 8；可空: 是
     */
    @TableField("company_curr_code")
    @Excel(name = "公司代码", width = 15)
    @Schema(description = "公司代码")
    private String companyCurrCode;

    /**
     * 描述；字段名: CURR_DESC；数据类型: VARCHAR2；长度: 128；可空: 是
     */
    @TableField("curr_desc")
    @Excel(name = "描述", width = 15)
    @Schema(description = "描述")
    private String currDesc;

    /**
     * 关检融合码；字段名: CUSCIQ_CODE；数据类型: VARCHAR2；长度: 8；可空: 是
     */
    @TableField("cusciq_code")
    @Excel(name = "关检融合码", width = 15)
    @Schema(description = "关检融合码")
    private String cusciqCode;

}
