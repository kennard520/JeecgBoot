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
 * 征免方式 ParaLevymode。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "征免方式")
@TableName("PARA_LEVYMODE")
public class ParaLevymode implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: DUTY_MODE；数据类型: VARCHAR2；长度: 16；可空: 是
     */
    @TableField("duty_mode")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String dutyMode;

    /**
     * 名称；字段名: DUTY_SPEC；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("duty_spec")
    @Excel(name = "名称", width = 15)
    @Schema(description = "名称")
    private String dutySpec;

}
