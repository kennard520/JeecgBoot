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
 * 海关关别 ParaCustomsRel。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "海关关别")
@TableName("PARA_CUSTOMS_REL")
public class ParaCustomsRel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: CUSTOMS_CODE；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("customs_code")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String customsCode;

    /**
     * 中文名称；字段名: CUSTOMS_NAME；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("customs_name")
    @Excel(name = "中文名称", width = 15)
    @Schema(description = "中文名称")
    private String customsName;

    /**
     * 英文名称；字段名: CUSTOMS_E_NAME；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("customs_e_name")
    @Excel(name = "英文名称", width = 15)
    @Schema(description = "英文名称")
    private String customsEName;

    /**
     * 国别；字段名: COUNTRY_CODE；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("country_code")
    @Excel(name = "国别", width = 15)
    @Schema(description = "国别")
    private String countryCode;

}
