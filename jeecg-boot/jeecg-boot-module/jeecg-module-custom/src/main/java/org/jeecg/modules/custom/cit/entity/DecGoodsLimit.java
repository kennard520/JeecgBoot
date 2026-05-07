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

/**
 * 许可证信息表 DecGoodsLimit。
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
@Schema(description = "许可证信息表 DecGoodsLimit")
@TableName("DEC_GOODS_LIMIT")
public class DecGoodsLimit implements Serializable {
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
     * 报关单商品表体ID，父表关联ID
     */
    @TableField("dec_list_id")
    @Excel(name = "报关单商品表体ID，父表关联ID", width = 15)
    @Schema(description = "报关单商品表体ID，父表关联ID")
    private Long decListId;
    /**
     * 商品序号；字段代码: GoodsNo；数据类型: Integer；长度: 9；暂存必填: 是；申报必填: 是
     */
    @TableField("goods_no")
    @Excel(name = "商品序号", width = 15)
    @Schema(description = "商品序号")
    private Integer goodsNo;
    /**
     * 许可证类别代码；字段代码: LicTypeCode；数据类型: String；长度: 5；暂存必填: 否；申报必填: 是
     */
    @TableField("lic_type_code")
    @Excel(name = "许可证类别代码", width = 15)
    @Schema(description = "许可证类别代码")
    private String licTypeCode;
    /**
     * 许可证编号；字段代码: LicenceNo；数据类型: String；长度: 40；暂存必填: 否；申报必填: 是
     */
    @TableField("licence_no")
    @Excel(name = "许可证编号", width = 15)
    @Schema(description = "许可证编号")
    private String licenceNo;
    /**
     * 许可证核销明细序号；字段代码: LicWrtofDetailNo；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否
     */
    @TableField("lic_wrtof_detail_no")
    @Excel(name = "许可证核销明细序号", width = 15)
    @Schema(description = "许可证核销明细序号")
    private String licWrtofDetailNo;
    /**
     * 许可证核销数量；字段代码: LicWrtofQty；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否
     */
    @TableField("lic_wrtof_qty")
    @Excel(name = "许可证核销数量", width = 15)
    @Schema(description = "许可证核销数量")
    private String licWrtofQty;
    /**
     * 许可证核销数量单位；字段代码: LicWrtofQtyUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("lic_wrtof_qty_unit")
    @Excel(name = "许可证核销数量单位", width = 15)
    @Schema(description = "许可证核销数量单位")
    private String licWrtofQtyUnit;
}
