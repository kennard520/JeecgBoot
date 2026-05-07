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
import java.math.BigDecimal;

/**
 * 计量单位 ParaUnit。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "计量单位")
@TableName("PARA_UNIT")
public class ParaUnit implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: UNIT_CODE；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("unit_code")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String unitCode;

    /**
     * 名称；字段名: UNIT_NAME；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("unit_name")
    @Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String unitName;

    /**
     * 转换码；字段名: CONV_CODE；数据类型: VARCHAR2；长度: 2；可空: 是
     */
    @TableField("conv_code")
    @Excel(name = "转换码", width = 15)
    @Schema(description = "转换码")
    private String convCode;

    /**
     * 公司单位和海关单位转换率；字段名: CONV_RATIO；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("conv_ratio")
    @Excel(name = "公司单位和海关单位转换率", width = 15)
    @Schema(description = "公司单位和海关单位转换率")
    private BigDecimal convRatio;

    /**
     * 公司计量单位代码；字段名: COMPANY_UNIT_CODE；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("company_unit_code")
    @Excel(name = "公司计量单位代码", width = 15)
    @Schema(description = "公司计量单位代码")
    private String companyUnitCode;

    /**
     * 公司计量单位名称；字段名: COMPANY_UNIT_NAME；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("company_unit_name")
    @Excel(name = "公司计量单位名称", width = 15)
    @Schema(description = "公司计量单位名称")
    private String companyUnitName;

}
