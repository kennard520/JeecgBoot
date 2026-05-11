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
 * 进口/出口报关单表头 DecHead。
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
@Schema(description = "进口/出口报关单表头 DecHead")
@TableName("DEC_HEAD")
public class DecHead implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，达梦自增标识列
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID，达梦自增标识列", width = 15)
    @Schema(description = "主键ID，达梦自增标识列")
    private Long id;
    /**
     * 申报单位代码；字段代码: AgentCode；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是
     */
    @TableField("agent_code")
    @Excel(name = "申报单位代码", width = 15)
    @Schema(description = "申报单位代码")
    private String agentCode;
    /**
     * 申报单位名称；字段代码: AgentName；数据类型: String；长度: 100；暂存必填: 是；申报必填: 是
     */
    @TableField("agent_name")
    @Excel(name = "申报单位名称", width = 15)
    @Schema(description = "申报单位名称")
    private String agentName;
    /**
     * 批准文号；字段代码: ApprNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否；说明: 实填“外汇核销单号”
     */
    @TableField("appr_no")
    @Excel(name = "批准文号", width = 15)
    @Schema(description = "批准文号")
    private String apprNo;
    /**
     * 提单号；字段代码: BillNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("bill_no")
    @Excel(name = "提单号", width = 15)
    @Schema(description = "提单号")
    private String billNo;
    /**
     * 合同号；字段代码: ContrNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("contr_no")
    @Excel(name = "合同号", width = 15)
    @Schema(description = "合同号")
    private String contrNo;
    /**
     * 录入单位代码；字段代码: CopCode；数据类型: String；长度: 9；暂存必填: 是；申报必填: 是；说明: 预录入企业组织机构编码，导入暂存时，必填
     */
    @TableField("cop_code")
    @Excel(name = "录入单位代码", width = 15)
    @Schema(description = "录入单位代码")
    private String copCode;
    /**
     * 录入单位名称；字段代码: CopName；数据类型: String；长度: 70；暂存必填: 是；申报必填: 是；说明: 预录入企业名称，导入暂存时，必填
     */
    @TableField("cop_name")
    @Excel(name = "录入单位名称", width = 15)
    @Schema(description = "录入单位名称")
    private String copName;
    /**
     * 主管海关（申报地海关）；字段代码: CustomMaster；数据类型: String；长度: 4；暂存必填: 是；申报必填: 是
     */
    @TableField("custom_master")
    @Excel(name = "主管海关（申报地海关）", width = 15)
    @Schema(description = "主管海关（申报地海关）")
    private String customMaster;
    /**
     * 征免性质；字段代码: CutMode；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("cut_mode")
    @Excel(name = "征免性质", width = 15)
    @Schema(description = "征免性质")
    private String cutMode;
    /**
     * 扩展字段；字段代码: DataSource；数据类型: String；长度: 5；暂存必填: 否；申报必填: 否；说明: 扩展字段。 第一位：仅出口非医用口罩填写，申报必填； 1：国外标准 2：国内标准 第二位：无实际意义，空字符占位 第三位：表示商品拆分 空/0：不拆分 1：拆分 第四位：无实际意义，空字符占位。 第五位：表示智能申报标记： 空/0：非智能申报 1：增加智能申报 2：取消智能申报
     */
    @TableField("data_source")
    @Excel(name = "扩展字段", width = 15)
    @Schema(description = "扩展字段")
    private String dataSource;
    /**
     * 报关/转关关系标志；字段代码: DeclTrnRel；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 报关/转关关系标志。 0：一般报关单 1：转关提前报关单
     */
    @TableField("decl_trn_rel")
    @Excel(name = "报关/转关关系标志", width = 15)
    @Schema(description = "报关/转关关系标志")
    private String declTrnRel;
    /**
     * 经停港/指运港；字段代码: DistinatePort；数据类型: String；长度: 6；暂存必填: 否；申报必填: 是
     */
    @TableField("distinate_port")
    @Excel(name = "经停港/指运港", width = 15)
    @Schema(description = "经停港/指运港")
    private String distinatePort;
    /**
     * 报关标志；字段代码: EdiId；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是；说明: 1：普通报关 3：北方转关提前 5：南方转关提前 6：普通报关，运输工具名称以‘◎’开头，南方H2000直转
     */
    @TableField("edi_id")
    @Excel(name = "报关标志", width = 15)
    @Schema(description = "报关标志")
    private String ediId;
    /**
     * 海关编号；字段代码: EntryId；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否
     */
    @TableField("entry_id")
    @Excel(name = "海关编号", width = 15)
    @Schema(description = "海关编号")
    private String entryId;
    /**
     * 报关单类型；字段代码: EntryType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是；说明: 0普通报关单，L为带报关单清单的报关单，W无纸报关类型，D既是清单又是无纸报关的情况，M：无纸化通关
     */
    @TableField("entry_type")
    @Excel(name = "报关单类型", width = 15)
    @Schema(description = "报关单类型")
    private String entryType;
    /**
     * 运费币制；字段代码: FeeCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("fee_curr")
    @Excel(name = "运费币制", width = 15)
    @Schema(description = "运费币制")
    private String feeCurr;
    /**
     * 运费标记；字段代码: FeeMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("fee_mark")
    @Excel(name = "运费标记", width = 15)
    @Schema(description = "运费标记")
    private String feeMark;
    /**
     * 运费／率；字段代码: FeeRate；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否
     */
    @TableField("fee_rate")
    @Excel(name = "运费／率", width = 15)
    @Schema(description = "运费／率")
    private BigDecimal feeRate;
    /**
     * 毛重；字段代码: GrossWet；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是
     */
    @TableField("gross_wet")
    @Excel(name = "毛重", width = 15)
    @Schema(description = "毛重")
    private BigDecimal grossWet;
    /**
     * 进出口日期；字段代码: IEDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd
     */
    @TableField("ie_date")
    @Excel(name = "进出口日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "进出口日期")
    private Date ieDate;
    /**
     * 进出口标志；字段代码: IEFlag；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 进出口标志。 “I”：进口。“E”：出口
     */
    @TableField("ie_flag")
    @Excel(name = "进出口标志", width = 15)
    @Schema(description = "进出口标志")
    private String ieFlag;
    /**
     * 进出境关别；字段代码: IEPort；数据类型: String；长度: 4；暂存必填: 否；申报必填: 是；说明: 需要代码转换
     */
    @TableField("ie_port")
    @Excel(name = "进出境关别", width = 15)
    @Schema(description = "进出境关别")
    private String iePort;
    /**
     * 录入员名称；字段代码: InputerName；数据类型: String；长度: 32；暂存必填: 是；申报必填: 是；说明: 导入暂存时，必填
     */
    @TableField("inputer_name")
    @Excel(name = "录入员名称", width = 15)
    @Schema(description = "录入员名称")
    private String inputerName;
    /**
     * 保险费币制；字段代码: InsurCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("insur_curr")
    @Excel(name = "保险费币制", width = 15)
    @Schema(description = "保险费币制")
    private String insurCurr;
    /**
     * 保险费标记；字段代码: InsurMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("insur_mark")
    @Excel(name = "保险费标记", width = 15)
    @Schema(description = "保险费标记")
    private String insurMark;
    /**
     * 保险费／率；字段代码: InsurRate；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否
     */
    @TableField("insur_rate")
    @Excel(name = "保险费／率", width = 15)
    @Schema(description = "保险费／率")
    private BigDecimal insurRate;
    /**
     * 许可证编号；字段代码: LicenseNo；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否
     */
    @TableField("license_no")
    @Excel(name = "许可证编号", width = 15)
    @Schema(description = "许可证编号")
    private String licenseNo;
    /**
     * 备案号；字段代码: ManualNo；数据类型: String；长度: 12；暂存必填: 否；申报必填: 否
     */
    @TableField("manual_no")
    @Excel(name = "备案号", width = 15)
    @Schema(description = "备案号")
    private String manualNo;
    /**
     * 净重；字段代码: NetWt；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是
     */
    @TableField("net_wt")
    @Excel(name = "净重", width = 15)
    @Schema(description = "净重")
    private BigDecimal netWt;
    /**
     * 备注；字段代码: NoteS；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否
     */
    @TableField("note_s")
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String noteS;
    /**
     * 杂费币制；字段代码: OtherCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("other_curr")
    @Excel(name = "杂费币制", width = 15)
    @Schema(description = "杂费币制")
    private String otherCurr;
    /**
     * 杂费标志；字段代码: OtherMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("other_mark")
    @Excel(name = "杂费标志", width = 15)
    @Schema(description = "杂费标志")
    private String otherMark;
    /**
     * 杂费／率；字段代码: OtherRate；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否
     */
    @TableField("other_rate")
    @Excel(name = "杂费／率", width = 15)
    @Schema(description = "杂费／率")
    private BigDecimal otherRate;
    /**
     * 消费使用/生产销售单位代码；字段代码: OwnerCode；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 10位或9位，或NO
     */
    @TableField("owner_code")
    @Excel(name = "消费使用/生产销售单位代码", width = 15)
    @Schema(description = "消费使用/生产销售单位代码")
    private String ownerCode;
    /**
     * 消费使用/生产销售单位名称；字段代码: OwnerName；数据类型: String；长度: 100；暂存必填: 否；申报必填: 是
     */
    @TableField("owner_name")
    @Excel(name = "消费使用/生产销售单位名称", width = 15)
    @Schema(description = "消费使用/生产销售单位名称")
    private String ownerName;
    /**
     * 件数；字段代码: PackNo；数据类型: Integer；长度: 9；暂存必填: 否；申报必填: 是
     */
    @TableField("pack_no")
    @Excel(name = "件数", width = 15)
    @Schema(description = "件数")
    private Integer packNo;
    /**
     * 申报人标识；字段代码: PartenerID；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 申报人姓名
     */
    @TableField("partener_id")
    @Excel(name = "申报人标识", width = 15)
    @Schema(description = "申报人标识")
    private String partenerId;
    /**
     * 打印日期；字段代码: PDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 预录入日期，格式为：yyyyMMdd
     */
    @TableField("p_date")
    @Excel(name = "打印日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "打印日期")
    private Date pDate;
    /**
     * 预录入编号；字段代码: PreEntryId；数据类型: String；长度: 9；暂存必填: 否；申报必填: 否
     */
    @TableField("pre_entry_id")
    @Excel(name = "预录入编号", width = 15)
    @Schema(description = "预录入编号")
    private String preEntryId;
    /**
     * 风险评估参数；字段代码: Risk；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 空值（上海专用）
     */
    @TableField("risk")
    @Excel(name = "风险评估参数", width = 15)
    @Schema(description = "风险评估参数")
    private String risk;
    /**
     * 数据中心统一编号；字段代码: SeqNo；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 唯一标识一票报关单。首次导入传空值，由系统自动生成并返回客户端。
     */
    @TableField("seq_no")
    @Excel(name = "数据中心统一编号", width = 15)
    @Schema(description = "数据中心统一编号")
    private String seqNo;
    /**
     * 关联单据号；字段代码: TgdNo；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 空值，预留字段
     */
    @TableField("tgd_no")
    @Excel(name = "关联单据号", width = 15)
    @Schema(description = "关联单据号")
    private String tgdNo;
    /**
     * 启运国/运抵国；字段代码: TradeCountry；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是；说明: （进口：起运国；出口：运抵国）
     */
    @TableField("trade_country")
    @Excel(name = "启运国/运抵国", width = 15)
    @Schema(description = "启运国/运抵国")
    private String tradeCountry;
    /**
     * 监管方式；字段代码: TradeMode；数据类型: String；长度: 4；暂存必填: 否；申报必填: 是
     */
    @TableField("trade_mode")
    @Excel(name = "监管方式", width = 15)
    @Schema(description = "监管方式")
    private String tradeMode;
    /**
     * 境内收发货人编号；字段代码: TradeCode；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是
     */
    @TableField("trade_code")
    @Excel(name = "境内收发货人编号", width = 15)
    @Schema(description = "境内收发货人编号")
    private String tradeCode;
    /**
     * 运输方式代码；字段代码: TrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是
     */
    @TableField("traf_mode")
    @Excel(name = "运输方式代码", width = 15)
    @Schema(description = "运输方式代码")
    private String trafMode;
    /**
     * 运输工具代码及名称；字段代码: TrafName；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否
     */
    @TableField("traf_name")
    @Excel(name = "运输工具代码及名称", width = 15)
    @Schema(description = "运输工具代码及名称")
    private String trafName;
    /**
     * 境内收发货人名称；字段代码: TradeName；数据类型: String；长度: 100；暂存必填: 否；申报必填: 是
     */
    @TableField("trade_name")
    @Excel(name = "境内收发货人名称", width = 15)
    @Schema(description = "境内收发货人名称")
    private String tradeName;
    /**
     * 成交方式；字段代码: TransMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是
     */
    @TableField("trans_mode")
    @Excel(name = "成交方式", width = 15)
    @Schema(description = "成交方式")
    private String transMode;
    /**
     * 单据类型；字段代码: Type；数据类型: String；长度: 6；暂存必填: 否；申报必填: 是；说明: 前两位：一般报关单填空值； ML：保税区进出境备案清单 SD: “属地申报，口岸验放”报关单 LY:两单一审备案清单； CL:汇总征税报关单； SS:”属地申报，属地验放”报关单 SW：税单无纸化 SZ：水运中转普通报关单 SM：水运中转保税区进出境备案清单 SL：水运中转两单一审备案清单 MF:公路舱单跨境快速通关报关单 MK: 公路舱单跨境快速通关备案清单 MT:TIR运输。 TT:纳税期限报关单； IT:分期纳税报关单； EX：低值快速货物报关单； EC：低值快速货物汇总征税报关单； MU:多式联运报关单 AL：极简备案清单； BL：次简备案清单； H1：径予放行简化申报（海南）； H2：二线出岛简化申报（海南）； 第三位： 0/空：整合申报一次录入； 5:新模式概要申报; 6:新模式完整申报。 第四位：无实际意义，空字符占位。 第五位：无实际意义，空字符占位。 第六位：无实际意义，空字符占位。 第七位： 填写Z表示组合港。
     */
    @TableField("type")
    @Excel(name = "单据类型", width = 15)
    @Schema(description = "单据类型")
    private String type;
    /**
     * 录入员IC卡号；字段代码: TypistNo；数据类型: String；长度: 30；暂存必填: 是；申报必填: 是；说明: 导入暂存时，必填
     */
    @TableField("typist_no")
    @Excel(name = "录入员IC卡号", width = 15)
    @Schema(description = "录入员IC卡号")
    private String typistNo;
    /**
     * 包装种类；字段代码: WrapType；数据类型: String；长度: 2；暂存必填: 否；申报必填: 是
     */
    @TableField("wrap_type")
    @Excel(name = "包装种类", width = 15)
    @Schema(description = "包装种类")
    private String wrapType;
    /**
     * 担保验放标志；字段代码: ChkSurety；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 担保验放：1：是、0：否
     */
    @TableField("chk_surety")
    @Excel(name = "担保验放标志", width = 15)
    @Schema(description = "担保验放标志")
    private String chkSurety;
    /**
     * 备案清单类型；字段代码: BillType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 自贸区特有的类型： 1：普通备案清单 2：先进区后报关 3：分送集报备案清单 4；分送集报报关单 注：当选择自贸区类型时，以下字段不可填写 表头：合同号、批准文件、内销比率 表体：用途、工缴费、成品货号 自由文本：监管仓号、报关员联系方式
     */
    @TableField("bill_type")
    @Excel(name = "备案清单类型", width = 15)
    @Schema(description = "备案清单类型")
    private String billType;
    /**
     * 录入单位统一编码；字段代码: CopCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否
     */
    @TableField("cop_code_scc")
    @Excel(name = "录入单位统一编码", width = 15)
    @Schema(description = "录入单位统一编码")
    private String copCodeScc;
    /**
     * 收发货人统一编码；字段代码: TradeCoScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是
     */
    @TableField("trade_co_scc")
    @Excel(name = "收发货人统一编码", width = 15)
    @Schema(description = "收发货人统一编码")
    private String tradeCoScc;
    /**
     * 申报代码统一编码；字段代码: AgentCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是
     */
    @TableField("agent_code_scc")
    @Excel(name = "申报代码统一编码", width = 15)
    @Schema(description = "申报代码统一编码")
    private String agentCodeScc;
    /**
     * 消费使用/生产销售单位单位统一编码；字段代码: OwnerCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是
     */
    @TableField("owner_code_scc")
    @Excel(name = "消费使用/生产销售单位单位统一编码", width = 15)
    @Schema(description = "消费使用/生产销售单位单位统一编码")
    private String ownerCodeScc;
    /**
     * 价格说明；字段代码: PromiseItmes；数据类型: String；长度: 32；暂存必填: 是；申报必填: 是；说明: 1-勾选、0-未选、9-空 第一位特殊关系确认 第二位价格影响确认 第三位支付特许权使用费确认 第四位公式定价确认 第五位暂定价格确认
     */
    @TableField("promise_itmes")
    @Excel(name = "价格说明", width = 15)
    @Schema(description = "价格说明")
    private String promiseItmes;
    /**
     * 业务事项：说明：1-勾选、0-未勾选 第一位税单无纸化 第二位担保验放 第三位跨境电商海外仓 第四位组合港 第五位自报自享
     */
    @TableField("business_items")
    @Excel(name = "业务事项", width = 15)
    @Schema(description = "业务事项")
    private String businessItems;
    /**
     * 贸易国别；字段代码: TradeAreaCode；数据类型: String；长度: 3；暂存必填: 是；申报必填: 是
     */
    @TableField("trade_area_code")
    @Excel(name = "贸易国别", width = 15)
    @Schema(description = "贸易国别")
    private String tradeAreaCode;
    /**
     * 查验分流；字段代码: CheckFlow；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 查验分流： 1：是查验分流 0：不是查验分流
     */
    @TableField("check_flow")
    @Excel(name = "查验分流", width = 15)
    @Schema(description = "查验分流")
    private String checkFlow;
    /**
     * 税收征管标记；字段代码: TaxAaminMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 税收征管标记：0：无 1：有
     */
    @TableField("tax_aamin_mark")
    @Excel(name = "税收征管标记", width = 15)
    @Schema(description = "税收征管标记")
    private String taxAaminMark;
    /**
     * 标记唛码；字段代码: MarkNo；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 标记及号码【本批货物的标记和号码】
     */
    @TableField("mark_no")
    @Excel(name = "标记唛码", width = 15)
    @Schema(description = "标记唛码")
    private String markNo;
    /**
     * 启运港代码；字段代码: DespPortCode；数据类型: String；长度: 8；暂存必填: 否；申报必填: 是；说明: 启运港代码【本批货物的启运口岸】
     */
    @TableField("desp_port_code")
    @Excel(name = "启运港代码", width = 15)
    @Schema(description = "启运港代码")
    private String despPortCode;
    /**
     * 入境口岸代码；字段代码: EntyPortCode；数据类型: String；长度: 8；暂存必填: 否；申报必填: 是；说明: 入境口岸代码【货物从运输工具卸离的第一个境内口岸】
     */
    @TableField("enty_port_code")
    @Excel(name = "入境口岸代码", width = 15)
    @Schema(description = "入境口岸代码")
    private String entyPortCode;
    /**
     * 存放地点；字段代码: GoodsPlace；数据类型: String；长度: 100；暂存必填: 否；申报必填: 是；说明: 货物存放地点【报检时货物的存放地点】
     */
    @TableField("goods_place")
    @Excel(name = "存放地点", width = 15)
    @Schema(description = "存放地点")
    private String goodsPlace;
    /**
     * B/L号；字段代码: BLNo；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 提货单号【本批货物的提货单或出库单号码】
     */
    @TableField("bl_no")
    @Excel(name = "B/L号", width = 15)
    @Schema(description = "B/L号")
    private String blNo;
    /**
     * 口岸检验检疫机关；字段代码: InspOrgCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用
     */
    @TableField("insp_org_code")
    @Excel(name = "口岸检验检疫机关", width = 15)
    @Schema(description = "口岸检验检疫机关")
    private String inspOrgCode;
    /**
     * 特种业务标识；字段代码: SpecDeclFlag；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 特种业务标识： 0-未勾选，1-选中 第一位：“国际赛事” 第二位：“特殊进出军工物资” 第三位：“国际援助物资” 第四位：“国际会议” 第五位：“直通放行” 第六位：“外交礼遇” 第七位：“转关
     */
    @TableField("spec_decl_flag")
    @Excel(name = "特种业务标识", width = 15)
    @Schema(description = "特种业务标识")
    private String specDeclFlag;
    /**
     * 目的地海关；字段代码: PurpOrgCode；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 目的地海关【入境货物流向的目的地检验检疫机构】，填写4位关区代码。
     */
    @TableField("purp_org_code")
    @Excel(name = "目的地海关", width = 15)
    @Schema(description = "目的地海关")
    private String purpOrgCode;
    /**
     * 启运日期；字段代码: DespDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 发货日期【本批拟发货的日期】 格式为：yyyyMMdd
     */
    @TableField("desp_date")
    @Excel(name = "启运日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "启运日期")
    private Date despDate;
    /**
     * 卸毕日期；字段代码: CmplDschrgDt；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 卸毕日期【本批货物全部卸离运输工具的日期】 格式为：yyyyMMdd
     */
    @TableField("cmpl_dschrg_dt")
    @Excel(name = "卸毕日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "卸毕日期")
    private Date cmplDschrgDt;
    /**
     * 关联号码；字段代码: CorrelationNo；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否
     */
    @TableField("correlation_no")
    @Excel(name = "关联号码", width = 15)
    @Schema(description = "关联号码")
    private String correlationNo;
    /**
     * 关联理由；字段代码: CorrelationReasonFlag；数据类型: String；长度: 2；暂存必填: 否；申报必填: 否；说明: 关联理由【关联报检号的关联理由】
     */
    @TableField("correlation_reason_flag")
    @Excel(name = "关联理由", width = 15)
    @Schema(description = "关联理由")
    private String correlationReasonFlag;
    /**
     * 领证机关；字段代码: VsaOrgCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用
     */
    @TableField("vsa_org_code")
    @Excel(name = "领证机关", width = 15)
    @Schema(description = "领证机关")
    private String vsaOrgCode;
    /**
     * 原集装箱标识；字段代码: OrigBoxFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 入境原集装箱装载直接到目的机构，【1：是；0：否】
     */
    @TableField("orig_box_flag")
    @Excel(name = "原集装箱标识", width = 15)
    @Schema(description = "原集装箱标识")
    private String origBoxFlag;
    /**
     * 申报人员姓名；字段代码: DeclareName；数据类型: String；长度: 50；暂存必填: 是；申报必填: 是
     */
    @TableField("declare_name")
    @Excel(name = "申报人员姓名", width = 15)
    @Schema(description = "申报人员姓名")
    private String declareName;
    /**
     * 无其他包装；字段代码: NoOtherPack；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 勾选 0-未选，有其他包装；1：选中，无其他包装。
     */
    @TableField("no_other_pack")
    @Excel(name = "无其他包装", width = 15)
    @Schema(description = "无其他包装")
    private String noOtherPack;
    /**
     * 检验检疫受理机关；字段代码: OrgCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用
     */
    @TableField("org_code")
    @Excel(name = "检验检疫受理机关", width = 15)
    @Schema(description = "检验检疫受理机关")
    private String orgCode;
    /**
     * 境外发货人代码；字段代码: OverseasConsignorCode；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 境外发货人代码
     */
    @TableField("overseas_consignor_code")
    @Excel(name = "境外发货人代码", width = 15)
    @Schema(description = "境外发货人代码")
    private String overseasConsignorCode;
    /**
     * 境外收发货人名称；字段代码: OverseasConsignorCname；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否；说明: 境外收发货人中文名称
     */
    @TableField("overseas_consignor_cname")
    @Excel(name = "境外收发货人名称", width = 15)
    @Schema(description = "境外收发货人名称")
    private String overseasConsignorCname;
    /**
     * 境外发货人名称（外文）；字段代码: OverseasConsignorEname；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 境外发货人名称（外文）
     */
    @TableField("overseas_consignor_ename")
    @Excel(name = "境外发货人名称（外文）", width = 15)
    @Schema(description = "境外发货人名称（外文）")
    private String overseasConsignorEname;
    /**
     * 境外收发货人地址；字段代码: OverseasConsignorAddr；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 境外收发货人地址
     */
    @TableField("overseas_consignor_addr")
    @Excel(name = "境外收发货人地址", width = 15)
    @Schema(description = "境外收发货人地址")
    private String overseasConsignorAddr;
    /**
     * 境外收货人编码；字段代码: OverseasConsigneeCode；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 境外收货人编码
     */
    @TableField("overseas_consignee_code")
    @Excel(name = "境外收货人编码", width = 15)
    @Schema(description = "境外收货人编码")
    private String overseasConsigneeCode;
    /**
     * 境外收货人名称(外文)；字段代码: OverseasConsigneeEname；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 境外收货人名称（外文）
     */
    @TableField("overseas_consignee_ename")
    @Excel(name = "境外收货人名称(外文)", width = 15)
    @Schema(description = "境外收货人名称(外文)")
    private String overseasConsigneeEname;
    /**
     * 境内收发货人名称（外文）；字段代码: DomesticConsigneeEname；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 境内收发货人名称（外文）
     */
    @TableField("domestic_consignee_ename")
    @Excel(name = "境内收发货人名称（外文）", width = 15)
    @Schema(description = "境内收发货人名称（外文）")
    private String domesticConsigneeEname;
    /**
     * EDI申报备注；字段代码: EdiRemark；数据类型: String；长度: 128；暂存必填: 否；申报必填: 否
     */
    @TableField("edi_remark")
    @Excel(name = "EDI申报备注", width = 15)
    @Schema(description = "EDI申报备注")
    private String ediRemark;
    /**
     * EDI申报备注2；字段代码: EdiRemark2；数据类型: String；长度: 128；暂存必填: 否；申报必填: 否
     */
    @TableField("edi_remark2")
    @Excel(name = "EDI申报备注2", width = 15)
    @Schema(description = "EDI申报备注2")
    private String ediRemark2;
    /**
     * 境内收发货人检验检疫编码；字段代码: TradeCiqCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 境内收发货人检验检疫编码
     */
    @TableField("trade_ciq_code")
    @Excel(name = "境内收发货人检验检疫编码", width = 15)
    @Schema(description = "境内收发货人检验检疫编码")
    private String tradeCiqCode;
    /**
     * 消费使用/生产销售单位检验检疫编码；字段代码: OwnerCiqCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 消费使用/生产销售单位检验检疫编码
     */
    @TableField("owner_ciq_code")
    @Excel(name = "消费使用/生产销售单位检验检疫编码", width = 15)
    @Schema(description = "消费使用/生产销售单位检验检疫编码")
    private String ownerCiqCode;
    /**
     * 申报单位检验检疫编码；字段代码: DeclCiqCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 申报单位检验检疫编码
     */
    @TableField("decl_ciq_code")
    @Excel(name = "申报单位检验检疫编码", width = 15)
    @Schema(description = "申报单位检验检疫编码")
    private String declCiqCode;
}
