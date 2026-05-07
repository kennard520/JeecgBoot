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
 * 港口 ParaPortLin。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "港口")
@TableName("PARA_PORT_LIN")
public class ParaPortLin implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 256；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: PORT_CODE；数据类型: VARCHAR2；长度: 24；可空: 是
     */
    @TableField("port_code")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String portCode;

    /**
     * 中文名称；字段名: PORT_C_COD；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("port_c_cod")
    @Excel(name = "中文名称", width = 15)
    @Schema(description = "中文名称")
    private String portCCod;

    /**
     * 英文名称；字段名: PORT_E_COD；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("port_e_cod")
    @Excel(name = "英文名称", width = 15)
    @Schema(description = "英文名称")
    private String portECod;

    /**
     * PORT_LINE；字段名: PORT_LINE；数据类型: VARCHAR2；长度: 16；可空: 是
     */
    @TableField("port_line")
    @Excel(name = "PORT_LINE", width = 15)
    @Schema(description = "PORT_LINE")
    private String portLine;

    /**
     * PORT_COUNT；字段名: PORT_COUNT；数据类型: VARCHAR2；长度: 20；可空: 是
     */
    @TableField("port_count")
    @Excel(name = "PORT_COUNT", width = 15)
    @Schema(description = "PORT_COUNT")
    private String portCount;

    /**
     * QUARANTINE；字段名: QUARANTINE；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("quarantine")
    @Excel(name = "QUARANTINE", width = 15)
    @Schema(description = "QUARANTINE")
    private String quarantine;

    /**
     * 排序码；字段名: PK_SEQ；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("pk_seq")
    @Excel(name = "排序码", width = 15)
    @Schema(description = "排序码")
    private Integer pkSeq;

    /**
     * 关检融合码；字段名: CUSCIQ_CODE；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("cusciq_code")
    @Excel(name = "关检融合码", width = 15)
    @Schema(description = "关检融合码")
    private String cusciqCode;

    /**
     * 国别；字段名: COUNTRY_CODE；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("country_code")
    @Excel(name = "国别", width = 15)
    @Schema(description = "国别")
    private String countryCode;

}
