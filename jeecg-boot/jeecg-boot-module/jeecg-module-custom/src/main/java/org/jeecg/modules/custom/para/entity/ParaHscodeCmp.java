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
 * 商品编码变化 ParaHscodeCmp。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "商品编码变化")
@TableName("PARA_HSCODE_CMP")
public class ParaHscodeCmp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 比较时间点；字段名: FROM_DATE；数据类型: DATE；长度: 3；可空: 否
     */
    @TableField("from_date")
    @Excel(name = "比较时间点", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "比较时间点")
    private Date fromDate;

    /**
     * 被比较时间点；字段名: TO_DATE；数据类型: DATE；长度: 3；可空: 否
     */
    @TableField("to_date")
    @Excel(name = "被比较时间点", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "被比较时间点")
    private Date toDate;

    /**
     * 商品编码；字段名: HSCODE；数据类型: VARCHAR2；长度: 16；可空: 否
     */
    @TableField("hscode")
    @Excel(name = "商品编码", width = 15)
    @Schema(description = "商品编码")
    private String hscode;

    /**
     * 差异点；字段名: C_DIFF；数据类型: VARCHAR2；长度: 256；可空: 否
     */
    @TableField("c_diff")
    @Excel(name = "差异点", width = 15)
    @Schema(description = "差异点")
    private String cDiff;

    /**
     * 目前值；字段名: FROM_DATA；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("from_data")
    @Excel(name = "目前值", width = 15)
    @Schema(description = "目前值")
    private String fromData;

    /**
     * 原来值；字段名: TO_DATA；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("to_data")
    @Excel(name = "原来值", width = 15)
    @Schema(description = "原来值")
    private String toData;

}
