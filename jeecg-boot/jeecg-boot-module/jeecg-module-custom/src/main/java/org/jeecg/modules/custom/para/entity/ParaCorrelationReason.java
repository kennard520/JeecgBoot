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
 * 关联理由表 ParaCorrelationReason。
 *
 * <p>维护报关单表头关联理由字段的可选值，页面下拉以 code 作为保存值、text 作为显示文本。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "关联理由表")
@TableName("PARA_CORRELATION_REASON")
public class ParaCorrelationReason implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键ID", width = 15)
    @Schema(description = "主键ID")
    private String id;

    /**
     * 关联理由代码
     */
    @TableField("code")
    @Excel(name = "关联理由代码", width = 15)
    @Schema(description = "关联理由代码")
    private String code;

    /**
     * 关联理由名称
     */
    @TableField("text")
    @Excel(name = "关联理由名称", width = 20)
    @Schema(description = "关联理由名称")
    private String text;
}
