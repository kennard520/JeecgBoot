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
 * 进口/出口报关单表体 DecList。
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
@Schema(description = "进口/出口报关单表体 DecList")
@TableName("DEC_LIST")
public class DecList implements Serializable {
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
     * 归类标志；字段代码: ClassMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空值
     */
    @TableField("class_mark")
    @Excel(name = "归类标志", width = 15)
    @Schema(description = "归类标志")
    private String classMark;
    /**
     * 商品编号；字段代码: CodeTS；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是
     */
    @TableField("code_ts")
    @Excel(name = "商品编号", width = 15)
    @Schema(description = "商品编号")
    private String codeTs;
    /**
     * 备案序号；字段代码: ContrItem；数据类型: Integer；长度: 19；暂存必填: 否；申报必填: 否；说明: 程序控制9位
     */
    @TableField("contr_item")
    @Excel(name = "备案序号", width = 15)
    @Schema(description = "备案序号")
    private Integer contrItem;
    /**
     * 申报单价；字段代码: DeclPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 是
     */
    @TableField("decl_price")
    @Excel(name = "申报单价", width = 15)
    @Schema(description = "申报单价")
    private BigDecimal declPrice;
    /**
     * 申报总价；字段代码: DeclTotal；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 是
     */
    @TableField("decl_total")
    @Excel(name = "申报总价", width = 15)
    @Schema(description = "申报总价")
    private BigDecimal declTotal;
    /**
     * 征免方式；字段代码: DutyMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是
     */
    @TableField("duty_mode")
    @Excel(name = "征免方式", width = 15)
    @Schema(description = "征免方式")
    private String dutyMode;
    /**
     * 货号；字段代码: ExgNo；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("exg_no")
    @Excel(name = "货号", width = 15)
    @Schema(description = "货号")
    private String exgNo;
    /**
     * 版本号；字段代码: ExgVersion；数据类型: Integer；长度: 8；暂存必填: 否；申报必填: 否
     */
    @TableField("exg_version")
    @Excel(name = "版本号", width = 15)
    @Schema(description = "版本号")
    private Integer exgVersion;
    /**
     * 申报计量单位与法定单位比例因子；字段代码: Factor；数据类型: Decimal；长度: 11,3；暂存必填: 否；申报必填: 否
     */
    @TableField("factor")
    @Excel(name = "申报计量单位与法定单位比例因子", width = 15)
    @Schema(description = "申报计量单位与法定单位比例因子")
    private BigDecimal factor;
    /**
     * 第一计量单位（法定单位）；字段代码: FirstUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是
     */
    @TableField("first_unit")
    @Excel(name = "第一计量单位（法定单位）", width = 15)
    @Schema(description = "第一计量单位（法定单位）")
    private String firstUnit;
    /**
     * 第一法定数量；字段代码: FirstQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是
     */
    @TableField("first_qty")
    @Excel(name = "第一法定数量", width = 15)
    @Schema(description = "第一法定数量")
    private BigDecimal firstQty;
    /**
     * 成交计量单位；字段代码: GUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是
     */
    @TableField("g_unit")
    @Excel(name = "成交计量单位", width = 15)
    @Schema(description = "成交计量单位")
    private String gUnit;
    /**
     * 商品规格、型号；字段代码: GModel；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否
     */
    @TableField("g_model")
    @Excel(name = "商品规格、型号", width = 15)
    @Schema(description = "商品规格、型号")
    private String gModel;
    /**
     * 商品名称；字段代码: GName；数据类型: String；长度: 255；暂存必填: 否；申报必填: 是
     */
    @TableField("g_name")
    @Excel(name = "商品名称", width = 15)
    @Schema(description = "商品名称")
    private String gName;
    /**
     * 商品序号；字段代码: GNo；数据类型: Integer；长度: 19；暂存必填: 是；申报必填: 是
     */
    @TableField("g_no")
    @Excel(name = "商品序号", width = 15)
    @Schema(description = "商品序号")
    private Integer gNo;
    /**
     * 成交数量；字段代码: GQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是
     */
    @TableField("g_qty")
    @Excel(name = "成交数量", width = 15)
    @Schema(description = "成交数量")
    private BigDecimal gQty;
    /**
     * 原产国；字段代码: OriginCountry；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是；说明: 对于出口报关单，表体的originalCountry需要填目的国，destinationCountry填的是原产国。 对于进口报关单，表体的originalCountry需要填原产国，destinationCountry填的是目的国。
     */
    @TableField("origin_country")
    @Excel(name = "原产国", width = 15)
    @Schema(description = "原产国")
    private String originCountry;
    /**
     * 第二计量单位；字段代码: SecondUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("second_unit")
    @Excel(name = "第二计量单位", width = 15)
    @Schema(description = "第二计量单位")
    private String secondUnit;
    /**
     * 第二法定数量；字段代码: SecondQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是
     */
    @TableField("second_qty")
    @Excel(name = "第二法定数量", width = 15)
    @Schema(description = "第二法定数量")
    private BigDecimal secondQty;
    /**
     * 成交币制；字段代码: TradeCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是
     */
    @TableField("trade_curr")
    @Excel(name = "成交币制", width = 15)
    @Schema(description = "成交币制")
    private String tradeCurr;
    /**
     * 用途/生产厂家；字段代码: UseTo；数据类型: String；长度: 2；暂存必填: 否；申报必填: 否
     */
    @TableField("use_to")
    @Excel(name = "用途/生产厂家", width = 15)
    @Schema(description = "用途/生产厂家")
    private String useTo;
    /**
     * 工缴费；字段代码: WorkUsd；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否
     */
    @TableField("work_usd")
    @Excel(name = "工缴费", width = 15)
    @Schema(description = "工缴费")
    private BigDecimal workUsd;
    /**
     * 最终目的国（地区）；字段代码: DestinationCountry；数据类型: String；长度: 3；暂存必填: 是；申报必填: 是；说明: 对于出口报关单，表体的originalCountry需要填目的国，destinationCountry填的是原产国。 对于进口报关单，表体的originalCountry需要填原产国，destinationCountry填的是目的国。
     */
    @TableField("destination_country")
    @Excel(name = "最终目的国（地区）", width = 15)
    @Schema(description = "最终目的国（地区）")
    private String destinationCountry;
    /**
     * 监管类别名称；字段代码: CiqCode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 填写3位监管类别
     */
    @TableField("ciq_code")
    @Excel(name = "监管类别名称", width = 15)
    @Schema(description = "监管类别名称")
    private String ciqCode;
    /**
     * 商品英文名称；字段代码: DeclGoodsEname；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 申报货物名称(外文)【货物的外文名称】
     */
    @TableField("decl_goods_ename")
    @Excel(name = "商品英文名称", width = 15)
    @Schema(description = "商品英文名称")
    private String declGoodsEname;
    /**
     * 原产地区代码；字段代码: OrigPlaceCode；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 原产地区代码【原产国内的生产区域，如州、省等】
     */
    @TableField("orig_place_code")
    @Excel(name = "原产地区代码", width = 15)
    @Schema(description = "原产地区代码")
    private String origPlaceCode;
    /**
     * 用途代码；字段代码: Purpose；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 用途【本项货物的用途】
     */
    @TableField("purpose")
    @Excel(name = "用途代码", width = 15)
    @Schema(description = "用途代码")
    private String purpose;
    /**
     * 产品有效期；字段代码: ProdValidDt；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 产品有效期【质量保障的截止日期】 格式：yyyyMMdd
     */
    @TableField("prod_valid_dt")
    @Excel(name = "产品有效期", width = 15)
    @Schema(description = "产品有效期")
    private String prodValidDt;
    /**
     * 产品保质期；字段代码: ProdQgp；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 产品保质期【质量保障的月数或天数】
     */
    @TableField("prod_qgp")
    @Excel(name = "产品保质期", width = 15)
    @Schema(description = "产品保质期")
    private String prodQgp;
    /**
     * 货物属性代码；字段代码: GoodsAttr；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 货物属性【声明本项货物的相关属性】
     */
    @TableField("goods_attr")
    @Excel(name = "货物属性代码", width = 15)
    @Schema(description = "货物属性代码")
    private String goodsAttr;
    /**
     * 成份/原料/组份；字段代码: Stuff；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 成份/原料/组份【本项货物含有的成份或者货物原料,或化学品组份】
     */
    @TableField("stuff")
    @Excel(name = "成份/原料/组份", width = 15)
    @Schema(description = "成份/原料/组份")
    private String stuff;
    /**
     * UN编码；字段代码: Uncode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: UN编码【危险品货物对应的《危险化学品目录》中的UN编码】
     */
    @TableField("uncode")
    @Excel(name = "UN编码", width = 15)
    @Schema(description = "UN编码")
    private String uncode;
    /**
     * 危险货物名称；字段代码: DangName；数据类型: String；长度: 80；暂存必填: 否；申报必填: 否；说明: 危险品名称【危险品货物对应的《危险化学品目录》中的名称】
     */
    @TableField("dang_name")
    @Excel(name = "危险货物名称", width = 15)
    @Schema(description = "危险货物名称")
    private String dangName;
    /**
     * 危包类别；字段代码: DangPackType；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 危包类别【危险化学品包装类别】
     */
    @TableField("dang_pack_type")
    @Excel(name = "危包类别", width = 15)
    @Schema(description = "危包类别")
    private String dangPackType;
    /**
     * 危包规格；字段代码: DangPackSpec；数据类型: String；长度: 24；暂存必填: 否；申报必填: 否；说明: 危包规格【危险化学品包装规格】
     */
    @TableField("dang_pack_spec")
    @Excel(name = "危包规格", width = 15)
    @Schema(description = "危包规格")
    private String dangPackSpec;
    /**
     * 境外生产企业名称；字段代码: EngManEntCnm；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 境外生产企业名称
     */
    @TableField("eng_man_ent_cnm")
    @Excel(name = "境外生产企业名称", width = 15)
    @Schema(description = "境外生产企业名称")
    private String engManEntCnm;
    /**
     * 非危险化学品；字段代码: NoDangFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 危险品
     */
    @TableField("no_dang_flag")
    @Excel(name = "非危险化学品", width = 15)
    @Schema(description = "非危险化学品")
    private String noDangFlag;
    /**
     * 目的地代码；字段代码: DestCode；数据类型: String；长度: 8；暂存必填: 否；申报必填: 否；说明: 目的地代码【货物在境内预定最终抵达的交货地】
     */
    @TableField("dest_code")
    @Excel(name = "目的地代码", width = 15)
    @Schema(description = "目的地代码")
    private String destCode;
    /**
     * 检验检疫货物规格；字段代码: GoodsSpec；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 原检验检疫货物规格
     */
    @TableField("goods_spec")
    @Excel(name = "检验检疫货物规格", width = 15)
    @Schema(description = "检验检疫货物规格")
    private String goodsSpec;
    /**
     * 货物型号；字段代码: GoodsModel；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 货物型号【本项货物的所有型号】
     */
    @TableField("goods_model")
    @Excel(name = "货物型号", width = 15)
    @Schema(description = "货物型号")
    private String goodsModel;
    /**
     * 货物品牌；字段代码: GoodsBrand；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 货物品牌【本项货物的品牌】
     */
    @TableField("goods_brand")
    @Excel(name = "货物品牌", width = 15)
    @Schema(description = "货物品牌")
    private String goodsBrand;
    /**
     * 生产日期；字段代码: ProduceDate；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 生产日期【货物的生产日期或者生产批号】格式：yyyyMMdd,多个日期用英文半角分号分隔
     */
    @TableField("produce_date")
    @Excel(name = "生产日期", width = 15)
    @Schema(description = "生产日期")
    private String produceDate;
    /**
     * 生产批号；字段代码: ProdBatchNo；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 生产批号【货物的生产批号】
     */
    @TableField("prod_batch_no")
    @Excel(name = "生产批号", width = 15)
    @Schema(description = "生产批号")
    private String prodBatchNo;
    /**
     * 境内目的地/境内货源地；字段代码: DistrictCode；数据类型: String；长度: 5；暂存必填: 否；申报必填: 是；说明: 进口指境内目的地，出口指境内货源地
     */
    @TableField("district_code")
    @Excel(name = "境内目的地/境内货源地", width = 15)
    @Schema(description = "境内目的地/境内货源地")
    private String districtCode;
    /**
     * 检验检疫名称；字段代码: CiqName；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: CIQ代码对应的商品描述
     */
    @TableField("ciq_name")
    @Excel(name = "检验检疫名称", width = 15)
    @Schema(description = "检验检疫名称")
    private String ciqName;
    /**
     * 生产单位注册号；字段代码: MnufctrRegNo；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 出口独有
     */
    @TableField("mnufctr_reg_no")
    @Excel(name = "生产单位注册号", width = 15)
    @Schema(description = "生产单位注册号")
    private String mnufctrRegNo;
    /**
     * 生产单位名称；字段代码: MnufctrRegName；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否；说明: 出口独有
     */
    @TableField("mnufctr_reg_name")
    @Excel(name = "生产单位名称", width = 15)
    @Schema(description = "生产单位名称")
    private String mnufctrRegName;
    /**
     * 优惠贸易协定项下原产地；字段代码: RcepOrigPlaceCode；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否；说明: 优惠贸易协定项下原产地
     */
    @TableField("rcep_orig_place_code")
    @Excel(name = "优惠贸易协定项下原产地", width = 15)
    @Schema(description = "优惠贸易协定项下原产地")
    private String rcepOrigPlaceCode;
    /**
     * 禁限管制识别码；字段代码: ControlExplainCode；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 填写4位禁限管制识别码
     */
    @TableField("control_explain_code")
    @Excel(name = "禁限管制识别码", width = 15)
    @Schema(description = "禁限管制识别码")
    private String controlExplainCode;
}
