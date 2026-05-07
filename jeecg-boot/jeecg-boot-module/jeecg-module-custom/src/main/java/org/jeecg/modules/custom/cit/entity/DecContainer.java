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
 * 报关单集装箱 DecContainer。
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
@Schema(description = "报关单集装箱 DecContainer")
@TableName("DEC_CONTAINER")
public class DecContainer implements Serializable {
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
     * 集装箱号；字段代码: ContainerId；数据类型: String；长度: 32；暂存必填: 是；申报必填: 否；说明: 集装箱号
     */
    @TableField("container_id")
    @Excel(name = "集装箱号", width = 15)
    @Schema(description = "集装箱号")
    private String containerId;
    /**
     * 集装箱规格；字段代码: ContainerMd；数据类型: String；长度: 32；暂存必填: 是；申报必填: 否；说明: 集装箱规格
     */
    @TableField("container_md")
    @Excel(name = "集装箱规格", width = 15)
    @Schema(description = "集装箱规格")
    private String containerMd;
    /**
     * 商品项号；字段代码: GoodsNo；数据类型: String；长度: 255；暂存必填: 是；申报必填: 否；说明: 商品项号用半角逗号分隔，如“1,3”
     */
    @TableField("goods_no")
    @Excel(name = "商品项号", width = 15)
    @Schema(description = "商品项号")
    private String goodsNo;
    /**
     * 拼箱标识；字段代码: LclFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("lcl_flag")
    @Excel(name = "拼箱标识", width = 15)
    @Schema(description = "拼箱标识")
    private String lclFlag;
    /**
     * 箱货重量；字段代码: GoodsContaWt；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否
     */
    @TableField("goods_conta_wt")
    @Excel(name = "箱货重量", width = 15)
    @Schema(description = "箱货重量")
    private BigDecimal goodsContaWt;
    /**
     * 自重；字段代码: ContainerWt；数据类型: Decimal；长度: 18,5；暂存必填: 否；申报必填: 否
     */
    @TableField("container_wt")
    @Excel(name = "自重", width = 15)
    @Schema(description = "自重")
    private BigDecimal containerWt;
}
