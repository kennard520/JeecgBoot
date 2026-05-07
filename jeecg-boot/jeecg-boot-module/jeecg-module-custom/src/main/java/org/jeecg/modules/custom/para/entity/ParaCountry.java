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
 * 国别(地区) ParaCountry。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "国别(地区)")
@TableName("PARA_COUNTRY")
public class ParaCountry implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: COUNTRY_CO；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("country_co")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String countryCo;

    /**
     * 英文名称；字段名: COUNTRY_EN；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("country_en")
    @Excel(name = "英文名称", width = 15)
    @Schema(description = "英文名称")
    private String countryEn;

    /**
     * 中文名称；字段名: COUNTRY_NA；数据类型: VARCHAR2；长度: 128；可空: 是
     */
    @TableField("country_na")
    @Excel(name = "中文名称", width = 15)
    @Schema(description = "中文名称")
    private String countryNa;

    /**
     * 标识代码；字段名: EXAM_MARK；数据类型: VARCHAR2；长度: 1；可空: 是
     */
    @TableField("exam_mark")
    @Excel(name = "标识代码", width = 15)
    @Schema(description = "标识代码")
    private String examMark;

    /**
     * HIGH_LOW；字段名: HIGH_LOW；数据类型: VARCHAR2；长度: 1；可空: 是
     */
    @TableField("high_low")
    @Excel(name = "HIGH_LOW", width = 15)
    @Schema(description = "HIGH_LOW")
    private String highLow;

    /**
     * 英文简称；字段名: SHORT_EN；数据类型: VARCHAR2；长度: 16；可空: 是
     */
    @TableField("short_en")
    @Excel(name = "英文简称", width = 15)
    @Schema(description = "英文简称")
    private String shortEn;

    /**
     * 关检融合码；字段名: CUSCIQ_CODE；数据类型: VARCHAR2；长度: 16；可空: 是
     */
    @TableField("cusciq_code")
    @Excel(name = "关检融合码", width = 15)
    @Schema(description = "关检融合码")
    private String cusciqCode;

}
