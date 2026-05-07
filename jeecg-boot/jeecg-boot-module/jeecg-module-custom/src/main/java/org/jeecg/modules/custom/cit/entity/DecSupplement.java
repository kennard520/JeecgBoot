package org.jeecg.modules.custom.cit.entity;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 补充申报单信息DecSupplement。
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
@Schema(description = "补充申报单信息DecSupplement")
@TableName("DEC_SUPPLEMENT")
public class DecSupplement implements Serializable {
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
     * 补充申报单商品序号；字段代码: GNo；数据类型: Integer；长度: 2；暂存必填: 否；申报必填: 否
     */
    @TableField("g_no")
    @Excel(name = "补充申报单商品序号", width = 15)
    @Schema(description = "补充申报单商品序号")
    private Integer gNo;
    /**
     * 补充申报单类型；字段代码: SupType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("sup_type")
    @Excel(name = "补充申报单类型", width = 15)
    @Schema(description = "补充申报单类型")
    private String supType;
    /**
     * 品牌中文；字段代码: BrandCN；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否
     */
    @TableField("brand_cn")
    @Excel(name = "品牌中文", width = 15)
    @Schema(description = "品牌中文")
    private String brandCn;
    /**
     * 品牌英文；字段代码: BrandEN；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否
     */
    @TableField("brand_en")
    @Excel(name = "品牌英文", width = 15)
    @Schema(description = "品牌英文")
    private String brandEn;
    /**
     * 买方名称；字段代码: Buyer；数据类型: String；长度: 225；暂存必填: 否；申报必填: 否
     */
    @TableField("buyer")
    @Excel(name = "买方名称", width = 15)
    @Schema(description = "买方名称")
    private String buyer;
    /**
     * 买方联系人；字段代码: BuyerContact；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否
     */
    @TableField("buyer_contact")
    @Excel(name = "买方联系人", width = 15)
    @Schema(description = "买方联系人")
    private String buyerContact;
    /**
     * 买方地址；字段代码: BuyerAddr；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("buyer_addr")
    @Excel(name = "买方地址", width = 15)
    @Schema(description = "买方地址")
    private String buyerAddr;
    /**
     * 买方电话；字段代码: BuyerTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("buyer_tel")
    @Excel(name = "买方电话", width = 15)
    @Schema(description = "买方电话")
    private String buyerTel;
    /**
     * 卖方名称；字段代码: Seller；数据类型: String；长度: 225；暂存必填: 否；申报必填: 否
     */
    @TableField("seller")
    @Excel(name = "卖方名称", width = 15)
    @Schema(description = "卖方名称")
    private String seller;
    /**
     * 卖方联系人；字段代码: SellerContact；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否
     */
    @TableField("seller_contact")
    @Excel(name = "卖方联系人", width = 15)
    @Schema(description = "卖方联系人")
    private String sellerContact;
    /**
     * 卖方地址；字段代码: SellerAddr；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("seller_addr")
    @Excel(name = "卖方地址", width = 15)
    @Schema(description = "卖方地址")
    private String sellerAddr;
    /**
     * 卖方电话；字段代码: SellerTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("seller_tel")
    @Excel(name = "卖方电话", width = 15)
    @Schema(description = "卖方电话")
    private String sellerTel;
    /**
     * 生产厂商名称；字段代码: Factory；数据类型: String；长度: 225；暂存必填: 否；申报必填: 否
     */
    @TableField("factory")
    @Excel(name = "生产厂商名称", width = 15)
    @Schema(description = "生产厂商名称")
    private String factory;
    /**
     * 生产厂商联系人；字段代码: FactoryContact；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否
     */
    @TableField("factory_contact")
    @Excel(name = "生产厂商联系人", width = 15)
    @Schema(description = "生产厂商联系人")
    private String factoryContact;
    /**
     * 生产厂商地址；字段代码: FactoryAddr；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("factory_addr")
    @Excel(name = "生产厂商地址", width = 15)
    @Schema(description = "生产厂商地址")
    private String factoryAddr;
    /**
     * 生产厂商电话；字段代码: FactoryTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("factory_tel")
    @Excel(name = "生产厂商电话", width = 15)
    @Schema(description = "生产厂商电话")
    private String factoryTel;
    /**
     * 合同协议号；字段代码: ContrNo；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否
     */
    @TableField("contr_no")
    @Excel(name = "合同协议号", width = 15)
    @Schema(description = "合同协议号")
    private String contrNo;
    /**
     * 签约日期；字段代码: ContrDate；数据类型: Date；暂存必填: 否；申报必填: 否；说明: 签约日期格式：yyyyMMdd
     */
    @TableField("contr_date")
    @Excel(name = "签约日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "签约日期")
    private Date contrDate;
    /**
     * 发票编号；字段代码: InvoiceNo；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否
     */
    @TableField("invoice_no")
    @Excel(name = "发票编号", width = 15)
    @Schema(description = "发票编号")
    private String invoiceNo;
    /**
     * 发票日期；字段代码: InvoiceDate；数据类型: Date；暂存必填: 否；申报必填: 否；说明: 发票日期格式：yyyyMMdd
     */
    @TableField("invoice_date")
    @Excel(name = "发票日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "发票日期")
    private Date invoiceDate;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_BabRel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("i_bab_rel")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iBabRel;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_PriceEffect；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("i_price_effect")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iPriceEffect;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_PriceClose；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("i_price_close")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iPriceClose;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_OtherLimited；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("i_other_limited")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iOtherLimited;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_OtherEffect；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("i_other_effect")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iOtherEffect;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_Note1；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_note1")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iNote1;
    /**
     * 价格申报项，进口货物申报项。；字段代码: I_IsUsefee；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("i_is_usefee")
    @Excel(name = "价格申报项，进口货物申报项。", width = 15)
    @Schema(description = "价格申报项，进口货物申报项。")
    private String iIsUsefee;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_IsProfit；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("i_is_profit")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iIsProfit;
    /**
     * 价格申报项，进口货物申报项；字段代码: I_Note2；数据类型: String；长度: 600；暂存必填: 否；申报必填: 否
     */
    @TableField("i_note2")
    @Excel(name = "价格申报项，进口货物申报项", width = 15)
    @Schema(description = "价格申报项，进口货物申报项")
    private String iNote2;
    /**
     * 价格申报项，币制；字段代码: Curr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("curr")
    @Excel(name = "价格申报项，币制", width = 15)
    @Schema(description = "价格申报项，币制")
    private String curr;
    /**
     * 价格申报项，发票价格单位金额；字段代码: InvoicePrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("invoice_price")
    @Excel(name = "价格申报项，发票价格单位金额", width = 15)
    @Schema(description = "价格申报项，发票价格单位金额")
    private BigDecimal invoicePrice;
    /**
     * 价格申报项，发票价格总金额；字段代码: InvoiceAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("invoice_amount")
    @Excel(name = "价格申报项，发票价格总金额", width = 15)
    @Schema(description = "价格申报项，发票价格总金额")
    private BigDecimal invoiceAmount;
    /**
     * 价格申报项，发票价格备注；字段代码: InvoiceNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("invoice_note")
    @Excel(name = "价格申报项，发票价格备注", width = 15)
    @Schema(description = "价格申报项，发票价格备注")
    private String invoiceNote;
    /**
     * 价格申报项，间接支付/收取的货款单位金额，进口是间接支付，出口是间接收取；字段代码: GoodsPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("goods_price")
    @Excel(name = "价格申报项，间接支付/收取的货款单位金额，进口是间接支付，出口是间接收取", width = 15)
    @Schema(description = "价格申报项，间接支付/收取的货款单位金额，进口是间接支付，出口是间接收取")
    private BigDecimal goodsPrice;
    /**
     * 价格申报项，间接支付/收取的货款总金额，进口是间接支付，出口是间接收取；字段代码: GoodsAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("goods_amount")
    @Excel(name = "价格申报项，间接支付/收取的货款总金额，进口是间接支付，出口是间接收取", width = 15)
    @Schema(description = "价格申报项，间接支付/收取的货款总金额，进口是间接支付，出口是间接收取")
    private BigDecimal goodsAmount;
    /**
     * 价格申报项，间接支付/收取的货款备注，进口是间接支付，出口是间接收取；字段代码: GoodsNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("goods_note")
    @Excel(name = "价格申报项，间接支付/收取的货款备注，进口是间接支付，出口是间接收取", width = 15)
    @Schema(description = "价格申报项，间接支付/收取的货款备注，进口是间接支付，出口是间接收取")
    private String goodsNote;
    /**
     * 价格申报项，进口货物除购货佣金以外的佣金和经纪费单位金额；字段代码: I_CommissionPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_commission_price")
    @Excel(name = "价格申报项，进口货物除购货佣金以外的佣金和经纪费单位金额", width = 15)
    @Schema(description = "价格申报项，进口货物除购货佣金以外的佣金和经纪费单位金额")
    private BigDecimal iCommissionPrice;
    /**
     * 价格申报项，进口货物除购货佣金以外的佣金和经纪费总金额；字段代码: I_CommissionAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_commission_amount")
    @Excel(name = "价格申报项，进口货物除购货佣金以外的佣金和经纪费总金额", width = 15)
    @Schema(description = "价格申报项，进口货物除购货佣金以外的佣金和经纪费总金额")
    private BigDecimal iCommissionAmount;
    /**
     * 价格申报项，进口货物除购货佣金以外的佣金和经纪费备注；字段代码: I_CommissionNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_commission_note")
    @Excel(name = "价格申报项，进口货物除购货佣金以外的佣金和经纪费备注", width = 15)
    @Schema(description = "价格申报项，进口货物除购货佣金以外的佣金和经纪费备注")
    private String iCommissionNote;
    /**
     * 价格申报项，与该进口货物视为一体的容器费用单位金额；字段代码: I_ContainerPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_container_price")
    @Excel(name = "价格申报项，与该进口货物视为一体的容器费用单位金额", width = 15)
    @Schema(description = "价格申报项，与该进口货物视为一体的容器费用单位金额")
    private BigDecimal iContainerPrice;
    /**
     * 价格申报项，与该进口货物视为一体的容器费用总金额；字段代码: I_ContainerAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_container_amount")
    @Excel(name = "价格申报项，与该进口货物视为一体的容器费用总金额", width = 15)
    @Schema(description = "价格申报项，与该进口货物视为一体的容器费用总金额")
    private BigDecimal iContainerAmount;
    /**
     * 价格申报项，与该进口货物视为一体的容器费用备注；字段代码: I_ContainerNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_container_note")
    @Excel(name = "价格申报项，与该进口货物视为一体的容器费用备注", width = 15)
    @Schema(description = "价格申报项，与该进口货物视为一体的容器费用备注")
    private String iContainerNote;
    /**
     * 价格申报项，进口货物包装材料和包装劳务费用单位金额；字段代码: I_PackPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_pack_price")
    @Excel(name = "价格申报项，进口货物包装材料和包装劳务费用单位金额", width = 15)
    @Schema(description = "价格申报项，进口货物包装材料和包装劳务费用单位金额")
    private BigDecimal iPackPrice;
    /**
     * 价格申报项，进口货物包装材料和包装劳务费用总金额；字段代码: I_PackAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_pack_amount")
    @Excel(name = "价格申报项，进口货物包装材料和包装劳务费用总金额", width = 15)
    @Schema(description = "价格申报项，进口货物包装材料和包装劳务费用总金额")
    private BigDecimal iPackAmount;
    /**
     * 价格申报项，进口货物包装材料和包装劳务费用备注；字段代码: I_PackNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_pack_note")
    @Excel(name = "价格申报项，进口货物包装材料和包装劳务费用备注", width = 15)
    @Schema(description = "价格申报项，进口货物包装材料和包装劳务费用备注")
    private String iPackNote;
    /**
     * 价格申报项，进口货物包含的材料、部件、零件和类似货物单位金额；字段代码: I_PartPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_part_price")
    @Excel(name = "价格申报项，进口货物包含的材料、部件、零件和类似货物单位金额", width = 15)
    @Schema(description = "价格申报项，进口货物包含的材料、部件、零件和类似货物单位金额")
    private BigDecimal iPartPrice;
    /**
     * 价格申报项，进口货物包含的材料、部件、零件和类似货物总金额；字段代码: I_PartAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_part_amount")
    @Excel(name = "价格申报项，进口货物包含的材料、部件、零件和类似货物总金额", width = 15)
    @Schema(description = "价格申报项，进口货物包含的材料、部件、零件和类似货物总金额")
    private BigDecimal iPartAmount;
    /**
     * 价格申报项，进口货物包含的材料、部件、零件和类似货物备注；字段代码: I_PartNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_part_note")
    @Excel(name = "价格申报项，进口货物包含的材料、部件、零件和类似货物备注", width = 15)
    @Schema(description = "价格申报项，进口货物包含的材料、部件、零件和类似货物备注")
    private String iPartNote;
    /**
     * 价格申报项，在生产进口货物过程中使用的工具、模具和类似货物单位金额；字段代码: I_ToolPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_tool_price")
    @Excel(name = "价格申报项，在生产进口货物过程中使用的工具、模具和类似货物单位金额", width = 15)
    @Schema(description = "价格申报项，在生产进口货物过程中使用的工具、模具和类似货物单位金额")
    private BigDecimal iToolPrice;
    /**
     * 价格申报项，在生产进口货物过程中使用的工具、模具和类似货物总金额；字段代码: I_ToolAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_tool_amount")
    @Excel(name = "价格申报项，在生产进口货物过程中使用的工具、模具和类似货物总金额", width = 15)
    @Schema(description = "价格申报项，在生产进口货物过程中使用的工具、模具和类似货物总金额")
    private BigDecimal iToolAmount;
    /**
     * 价格申报项，在生产进口货物过程中使用的工具、模具和类似货物备注；字段代码: I_ToolNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_tool_note")
    @Excel(name = "价格申报项，在生产进口货物过程中使用的工具、模具和类似货物备注", width = 15)
    @Schema(description = "价格申报项，在生产进口货物过程中使用的工具、模具和类似货物备注")
    private String iToolNote;
    /**
     * 价格申报项，在生产进口货物过程中消耗的材料单位金额；字段代码: I_LossPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_loss_price")
    @Excel(name = "价格申报项，在生产进口货物过程中消耗的材料单位金额", width = 15)
    @Schema(description = "价格申报项，在生产进口货物过程中消耗的材料单位金额")
    private BigDecimal iLossPrice;
    /**
     * 价格申报项，在生产进口货物过程中消耗的材料总金额；字段代码: I_LossAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_loss_amount")
    @Excel(name = "价格申报项，在生产进口货物过程中消耗的材料总金额", width = 15)
    @Schema(description = "价格申报项，在生产进口货物过程中消耗的材料总金额")
    private BigDecimal iLossAmount;
    /**
     * 价格申报项，在生产进口货物过程中消耗的材料备注；字段代码: I_LossNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_loss_note")
    @Excel(name = "价格申报项，在生产进口货物过程中消耗的材料备注", width = 15)
    @Schema(description = "价格申报项，在生产进口货物过程中消耗的材料备注")
    private String iLossNote;
    /**
     * 价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务单位金额；字段代码: I_DesignPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_design_price")
    @Excel(name = "价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务单位金额", width = 15)
    @Schema(description = "价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务单位金额")
    private BigDecimal iDesignPrice;
    /**
     * 价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务总金额；字段代码: I_DesignAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_design_amount")
    @Excel(name = "价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务总金额", width = 15)
    @Schema(description = "价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务总金额")
    private BigDecimal iDesignAmount;
    /**
     * 价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务备注；字段代码: I_DesignNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_design_note")
    @Excel(name = "价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务备注", width = 15)
    @Schema(description = "价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务备注")
    private String iDesignNote;
    /**
     * 价格申报项，特许权使用费单位金额；字段代码: I_UsefeePrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_usefee_price")
    @Excel(name = "价格申报项，特许权使用费单位金额", width = 15)
    @Schema(description = "价格申报项，特许权使用费单位金额")
    private BigDecimal iUsefeePrice;
    /**
     * 价格申报项，特许权使用费总金额；字段代码: I_UsefeeAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_usefee_amount")
    @Excel(name = "价格申报项，特许权使用费总金额", width = 15)
    @Schema(description = "价格申报项，特许权使用费总金额")
    private BigDecimal iUsefeeAmount;
    /**
     * 价格申报项，特许权使用费备注；字段代码: I_UsefeeNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_usefee_note")
    @Excel(name = "价格申报项，特许权使用费备注", width = 15)
    @Schema(description = "价格申报项，特许权使用费备注")
    private String iUsefeeNote;
    /**
     * 价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益单位金额；字段代码: I_ProfitPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_profit_price")
    @Excel(name = "价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益单位金额", width = 15)
    @Schema(description = "价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益单位金额")
    private BigDecimal iProfitPrice;
    /**
     * 价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益总金额；字段代码: I_ProfitAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_profit_amount")
    @Excel(name = "价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益总金额", width = 15)
    @Schema(description = "价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益总金额")
    private BigDecimal iProfitAmount;
    /**
     * 价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益备注；字段代码: I_ProfitNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_profit_note")
    @Excel(name = "价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益备注", width = 15)
    @Schema(description = "价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益备注")
    private String iProfitNote;
    /**
     * 价格申报项，进口货物运输费用单位金额；字段代码: I_FeePrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_fee_price")
    @Excel(name = "价格申报项，进口货物运输费用单位金额", width = 15)
    @Schema(description = "价格申报项，进口货物运输费用单位金额")
    private BigDecimal iFeePrice;
    /**
     * 价格申报项，进口货物运输费用总金额；字段代码: I_FeeAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_fee_amount")
    @Excel(name = "价格申报项，进口货物运输费用总金额", width = 15)
    @Schema(description = "价格申报项，进口货物运输费用总金额")
    private BigDecimal iFeeAmount;
    /**
     * 价格申报项，进口货物运输费用备注；字段代码: I_FeeNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_fee_note")
    @Excel(name = "价格申报项，进口货物运输费用备注", width = 15)
    @Schema(description = "价格申报项，进口货物运输费用备注")
    private String iFeeNote;
    /**
     * 价格申报项，进口货物运输相关费用单位金额；字段代码: I_OtherPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_other_price")
    @Excel(name = "价格申报项，进口货物运输相关费用单位金额", width = 15)
    @Schema(description = "价格申报项，进口货物运输相关费用单位金额")
    private BigDecimal iOtherPrice;
    /**
     * 价格申报项，进口货物运输相关费用总金额；字段代码: I_OtherAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_other_amount")
    @Excel(name = "价格申报项，进口货物运输相关费用总金额", width = 15)
    @Schema(description = "价格申报项，进口货物运输相关费用总金额")
    private BigDecimal iOtherAmount;
    /**
     * 价格申报项，进口货物运输相关费用备注；字段代码: I_OtherNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_other_note")
    @Excel(name = "价格申报项，进口货物运输相关费用备注", width = 15)
    @Schema(description = "价格申报项，进口货物运输相关费用备注")
    private String iOtherNote;
    /**
     * 价格申报项，进口货物保险费单位金额；字段代码: I_InsurPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("i_insur_price")
    @Excel(name = "价格申报项，进口货物保险费单位金额", width = 15)
    @Schema(description = "价格申报项，进口货物保险费单位金额")
    private BigDecimal iInsurPrice;
    /**
     * 价格申报项，进口货物保险费总金额；字段代码: I_InsurAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否
     */
    @TableField("i_insur_amount")
    @Excel(name = "价格申报项，进口货物保险费总金额", width = 15)
    @Schema(description = "价格申报项，进口货物保险费总金额")
    private BigDecimal iInsurAmount;
    /**
     * 价格申报项，进口货物保险费备注；字段代码: I_InsurNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("i_insur_note")
    @Excel(name = "价格申报项，进口货物保险费备注", width = 15)
    @Schema(description = "价格申报项，进口货物保险费备注")
    private String iInsurNote;
    /**
     * 价格申报项，出口关税是否已经从申报价格中扣除；字段代码: E_IsDutyDel；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("e_is_duty_del")
    @Excel(name = "价格申报项，出口关税是否已经从申报价格中扣除", width = 15)
    @Schema(description = "价格申报项，出口关税是否已经从申报价格中扣除")
    private String eIsDutyDel;
    /**
     * 归类申报项，商品其他名称；字段代码: GNameOther；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否
     */
    @TableField("g_name_other")
    @Excel(name = "归类申报项，商品其他名称", width = 15)
    @Schema(description = "归类申报项，商品其他名称")
    private String gNameOther;
    /**
     * 归类申报项，进/出口国地区海关商品编码；字段代码: CodeTsOther；数据类型: String；长度: 15；暂存必填: 否；申报必填: 否
     */
    @TableField("code_ts_other")
    @Excel(name = "归类申报项，进/出口国地区海关商品编码", width = 15)
    @Schema(description = "归类申报项，进/出口国地区海关商品编码")
    private String codeTsOther;
    /**
     * 归类申报项；字段代码: IsClassDecision；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 该商品是否取得过海关预归类决定书： 0：否、1：是 如选择是，则后面三项必填。
     */
    @TableField("is_class_decision")
    @Excel(name = "归类申报项", width = 15)
    @Schema(description = "归类申报项")
    private String isClassDecision;
    /**
     * 归类申报项，预归类决定书编号；字段代码: DecisionNO；数据类型: String；长度: 45；暂存必填: 否；申报必填: 否
     */
    @TableField("decision_no")
    @Excel(name = "归类申报项，预归类决定书编号", width = 15)
    @Schema(description = "归类申报项，预归类决定书编号")
    private String decisionNo;
    /**
     * 归类申报项，预归类决定书商品编码；字段代码: CodeTsDecision；数据类型: String；长度: 15；暂存必填: 否；申报必填: 否
     */
    @TableField("code_ts_decision")
    @Excel(name = "归类申报项，预归类决定书商品编码", width = 15)
    @Schema(description = "归类申报项，预归类决定书商品编码")
    private String codeTsDecision;
    /**
     * 归类申报项，作出预归类决定的直属海关；字段代码: DecisionCus；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否
     */
    @TableField("decision_cus")
    @Excel(name = "归类申报项，作出预归类决定的直属海关", width = 15)
    @Schema(description = "归类申报项，作出预归类决定的直属海关")
    private String decisionCus;
    /**
     * 归类申报项，该商品是否曾被海关取样化验；字段代码: IsSampleTest；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 归类申报项，该商品是否曾被海关取样化验： 0：否、1：是
     */
    @TableField("is_sample_test")
    @Excel(name = "归类申报项，该商品是否曾被海关取样化验", width = 15)
    @Schema(description = "归类申报项，该商品是否曾被海关取样化验")
    private String isSampleTest;
    /**
     * 字段代码: GOptions；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否；说明: 归类申报项，商品信息选项： A成分及比例 B原料及组成 C生产加工工艺 D构成 E技术参数 F具体规格 G工作原理 H车型、排量 I功能 J用途 K加工程度 L性能指标 M其他信息 以上可多选，至少选择一项。 存储和上传方式：13位有效位，选择了A－M项的任何一个，则对应位为1，否则为0。
     */
    @TableField("g_options")
    @Excel(name = "字段代码: GOptions", width = 15)
    @Schema(description = "字段代码: GOptions")
    private String gOptions;
    /**
     * 运输方式；字段代码: TrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("traf_mode")
    @Excel(name = "运输方式", width = 15)
    @Schema(description = "运输方式")
    private String trafMode;
    /**
     * 原产地申报项，是否直接运输；字段代码: IsDirectTraf；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 原产地申报项，是否直接运输： 0：否、1：是
     */
    @TableField("is_direct_traf")
    @Excel(name = "原产地申报项，是否直接运输", width = 15)
    @Schema(description = "原产地申报项，是否直接运输")
    private String isDirectTraf;
    /**
     * 原产地申报项，中转国地区，如果选择非直接运输，必填；字段代码: TransitCountry；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否
     */
    @TableField("transit_country")
    @Excel(name = "原产地申报项，中转国地区，如果选择非直接运输，必填", width = 15)
    @Schema(description = "原产地申报项，中转国地区，如果选择非直接运输，必填")
    private String transitCountry;
    /**
     * 原产地申报项，到货口岸；字段代码: DestPort；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: CUSTOMS参数
     */
    @TableField("dest_port")
    @Excel(name = "原产地申报项，到货口岸", width = 15)
    @Schema(description = "原产地申报项，到货口岸")
    private String destPort;
    /**
     * 原产地申报项，申报口岸；字段代码: DeclPort；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: CUSTOMS参数
     */
    @TableField("decl_port")
    @Excel(name = "原产地申报项，申报口岸", width = 15)
    @Schema(description = "原产地申报项，申报口岸")
    private String declPort;
    /**
     * 原产地申报项，提单编号；字段代码: BillNo；数据类型: String；长度: 48；暂存必填: 否；申报必填: 否
     */
    @TableField("bill_no")
    @Excel(name = "原产地申报项，提单编号", width = 15)
    @Schema(description = "原产地申报项，提单编号")
    private String billNo;
    /**
     * 原产地申报项；字段代码: OriginCountry；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否；说明: COUNTRY参数
     */
    @TableField("origin_country")
    @Excel(name = "原产地申报项", width = 15)
    @Schema(description = "原产地申报项")
    private String originCountry;
    /**
     * 原产地申报项，原产国地区标记的位置；字段代码: OriginMark；数据类型: String；长度: 5；暂存必填: 否；申报必填: 否；说明: 原产地申报项，原产国地区标记的位置： 1：外包装、2：内包装、3：产品本体、4：无
     */
    @TableField("origin_mark")
    @Excel(name = "原产地申报项，原产国地区标记的位置", width = 15)
    @Schema(description = "原产地申报项，原产国地区标记的位置")
    private String originMark;
    /**
     * 原产地申报项，原产地证书签发机构及所在国家地区，非参数选项，可录入汉字或字母；字段代码: CertCountry；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("cert_country")
    @Excel(name = "原产地申报项，原产地证书签发机构及所在国家地区，非参数选项，可录入汉字或字母", width = 15)
    @Schema(description = "原产地申报项，原产地证书签发机构及所在国家地区，非参数选项，可录入汉字或字母")
    private String certCountry;
    /**
     * 原产地申报项，原产地证书编号；字段代码: CertNO；数据类型: String；长度: 45；暂存必填: 否；申报必填: 否
     */
    @TableField("cert_no")
    @Excel(name = "原产地申报项，原产地证书编号", width = 15)
    @Schema(description = "原产地申报项，原产地证书编号")
    private String certNo;
    /**
     * 原产地申报项，适用的原产地标准；字段代码: CertStandard；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 原产地申报项，适用的原产地标准： 1：完全获得、2：税号改变、 3：制造或加工工序、4：从价百分比、5：混合标准、6：其他标准
     */
    @TableField("cert_standard")
    @Excel(name = "原产地申报项，适用的原产地标准", width = 15)
    @Schema(description = "原产地申报项，适用的原产地标准")
    private String certStandard;
    /**
     * 公共申报项，其他需要说明的情况；字段代码: OtherNote；数据类型: String；长度: 1500；暂存必填: 否；申报必填: 否
     */
    @TableField("other_note")
    @Excel(name = "公共申报项，其他需要说明的情况", width = 15)
    @Schema(description = "公共申报项，其他需要说明的情况")
    private String otherNote;
    /**
     * 对以上申报内容是否需要海关予以保密；字段代码: IsSecret；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 对以上申报内容是否需要海关予以保密： 0：否 1：是
     */
    @TableField("is_secret")
    @Excel(name = "对以上申报内容是否需要海关予以保密", width = 15)
    @Schema(description = "对以上申报内容是否需要海关予以保密")
    private String isSecret;
    /**
     * 申报单位类型；字段代码: AgentType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 申报单位类型： 1：进出口货物收发货人 2：委托申报的报关企业
     */
    @TableField("agent_type")
    @Excel(name = "申报单位类型", width = 15)
    @Schema(description = "申报单位类型")
    private String agentType;
    /**
     * 申报人单位地址；字段代码: DeclAddr；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否
     */
    @TableField("decl_addr")
    @Excel(name = "申报人单位地址", width = 15)
    @Schema(description = "申报人单位地址")
    private String declAddr;
    /**
     * 申报人邮编；字段代码: DeclPost；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否
     */
    @TableField("decl_post")
    @Excel(name = "申报人邮编", width = 15)
    @Schema(description = "申报人邮编")
    private String declPost;
    /**
     * 申报人电话；字段代码: DeclTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("decl_tel")
    @Excel(name = "申报人电话", width = 15)
    @Schema(description = "申报人电话")
    private String declTel;
}
