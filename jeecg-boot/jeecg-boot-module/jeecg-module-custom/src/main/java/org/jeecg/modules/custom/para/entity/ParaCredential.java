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
 * 申请单证参数 ParaCredential。
 *
 * <p>用于维护单一窗口申请单证代码、名称以及默认正本/副本数量。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "申请单证参数")
@TableName("PARA_CREDENTIALS")
public class ParaCredential implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 申请单证代码；字段名: CODE；数据类型: VARCHAR2；长度: 50；可空: 否
     */
    @TableField("code")
    @Excel(name = "申请单证代码", width = 15)
    @Schema(description = "申请单证代码")
    private String code;

    /**
     * 申请单证名称；字段名: NAME；数据类型: VARCHAR2；长度: 200；可空: 否
     */
    @TableField("name")
    @Excel(name = "申请单证名称", width = 15)
    @Schema(description = "申请单证名称")
    private String name;

    /**
     * 正本数量；字段名: COUNT；数据类型: INT；可空: 是
     */
    @TableField("count")
    @Excel(name = "正本数量", width = 15)
    @Schema(description = "正本数量")
    private Integer count;

    /**
     * 副本数量；字段名: SUB_COUNT；数据类型: INT；可空: 是
     */
    @TableField("sub_count")
    @Excel(name = "副本数量", width = 15)
    @Schema(description = "副本数量")
    private Integer subCount;
}
