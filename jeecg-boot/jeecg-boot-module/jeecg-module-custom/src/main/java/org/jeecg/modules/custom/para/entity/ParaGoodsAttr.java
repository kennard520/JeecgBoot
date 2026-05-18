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
 * 货物属性 ParaGoodsAttr。
 *
 * <p>用于维护 DEC_LIST.GOODS_ATTR 对应的单一窗口货物属性代码。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "货物属性")
@TableName("PARA_GOODS_ATTR")
public class ParaGoodsAttr implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 货物属性代码；字段名: ATTR_CODE；数据类型: VARCHAR2；长度: 20；可空: 否
     */
    @TableField("attr_code")
    @Excel(name = "货物属性代码", width = 15)
    @Schema(description = "货物属性代码")
    private String attrCode;

    /**
     * 货物属性名称；字段名: ATTR_NAME；数据类型: VARCHAR2；长度: 100；可空: 否
     */
    @TableField("attr_name")
    @Excel(name = "货物属性名称", width = 15)
    @Schema(description = "货物属性名称")
    private String attrName;
}
