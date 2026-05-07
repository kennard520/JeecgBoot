package org.jeecg.modules.custom.cit.entity;

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
 * 其他包装信息表 DecOtherPack。
 *
 * <p>根据 {@code jeecg-boot/db/其他数据库脚本/达梦/CIT.sql} 生成，字段注释保留原脚本中的
 * 中文名、原字段代码、原数据类型、长度、暂存/申报必填状态和说明，便于后续对接海关 CIT 报文。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "其他包装信息表 DecOtherPack")
@TableName("DEC_OTHER_PACK")
public class DecOtherPack implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，达梦自增标识列
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID，达梦自增标识列", width = 15)
    @Schema(description = "主键ID，达梦自增标识列")
    private Long id;
    /**
     * 报关单表头ID，父表关联ID
     */
    @TableField("dec_head_id")
    @Excel(name = "报关单表头ID，父表关联ID", width = 15)
    @Schema(description = "报关单表头ID，父表关联ID")
    private Long decHeadId;
    /**
     * 包装件数；字段代码: PackQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是；说明: 根据包装种类确定，挂装、散装、裸装时可不输入
     */
    @TableField("pack_qty")
    @Excel(name = "包装件数", width = 15)
    @Schema(description = "包装件数")
    private BigDecimal packQty;
    /**
     * 包装材料种类；字段代码: PackType；数据类型: String；长度: 2；暂存必填: 否；申报必填: 是；说明: 包装材料种类
     */
    @TableField("pack_type")
    @Excel(name = "包装材料种类", width = 15)
    @Schema(description = "包装材料种类")
    private String packType;
}
