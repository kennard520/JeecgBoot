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
 * 征免性质 ParaLevytype。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "征免性质")
@TableName("PARA_LEVYTYPE")
public class ParaLevytype implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 256；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: CUT_MODE；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("cut_mode")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String cutMode;

    /**
     * 简称；字段名: ABBR_CUT；数据类型: VARCHAR2；长度: 128；可空: 是
     */
    @TableField("abbr_cut")
    @Excel(name = "简称", width = 15)
    @Schema(description = "简称")
    private String abbrCut;

    /**
     * 全称；字段名: FULL_CUT；数据类型: VARCHAR2；长度: 512；可空: 是
     */
    @TableField("full_cut")
    @Excel(name = "全称", width = 15)
    @Schema(description = "全称")
    private String fullCut;

}
