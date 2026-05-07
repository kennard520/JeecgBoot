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
 * 进口/出口提运单集装箱商品装配表 TrnContaGoodsList。
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
@Schema(description = "进口/出口提运单集装箱商品装配表 TrnContaGoodsList")
@TableName("TRN_CONTA_GOODS_LIST")
public class TrnContaGoodsList implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，达梦自增标识列
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID，达梦自增标识列", width = 15)
    @Schema(description = "主键ID，达梦自增标识列")
    private Long id;
    /**
     * 转关单集装箱ID，父表关联ID
     */
    @TableField("trn_container_id")
    @Excel(name = "转关单集装箱ID，父表关联ID", width = 15)
    @Schema(description = "转关单集装箱ID，父表关联ID")
    private Long trnContainerId;
    /**
     * 转关单提单ID，父表关联ID
     */
    @TableField("trn_list_id")
    @Excel(name = "转关单提单ID，父表关联ID", width = 15)
    @Schema(description = "转关单提单ID，父表关联ID")
    private Long trnListId;
    /**
     * 商品序号；字段代码: GNo；数据类型: Integer；长度: 9；暂存必填: 是；申报必填: 是
     */
    @TableField("g_no")
    @Excel(name = "商品序号", width = 15)
    @Schema(description = "商品序号")
    private Integer gNo;
    /**
     * 集装箱号；字段代码: ContaNo；数据类型: String；长度: 11；暂存必填: 是；申报必填: 是
     */
    @TableField("conta_no")
    @Excel(name = "集装箱号", width = 15)
    @Schema(description = "集装箱号")
    private String contaNo;
    /**
     * 商品件数；字段代码: ContaGoodsCount；数据类型: Integer；长度: 9；暂存必填: 否；申报必填: 是
     */
    @TableField("conta_goods_count")
    @Excel(name = "商品件数", width = 15)
    @Schema(description = "商品件数")
    private Integer contaGoodsCount;
    /**
     * 商品重量；字段代码: ContaGoodsWeight；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是
     */
    @TableField("conta_goods_weight")
    @Excel(name = "商品重量", width = 15)
    @Schema(description = "商品重量")
    private BigDecimal contaGoodsWeight;
}
