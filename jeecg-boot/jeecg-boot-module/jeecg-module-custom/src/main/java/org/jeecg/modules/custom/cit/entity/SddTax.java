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
 * 报税单SddTax。
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
@Schema(description = "报税单SddTax")
@TableName("SDD_TAX")
public class SddTax implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，达梦自增标识列
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID，达梦自增标识列", width = 15)
    @Schema(description = "主键ID，达梦自增标识列")
    private Long id;
    /**
     * 关联报关单表头ID，父表关联ID
     */
    @TableField("dec_head_id")
    @Excel(name = "关联报关单表头ID，父表关联ID", width = 15)
    @Schema(description = "关联报关单表头ID，父表关联ID")
    private Long decHeadId;
    /**
     * 文件名称；字段代码: FileName；数据类型: String；长度: 18；暂存必填: 否/否；申报必填: 否
     */
    @TableField("file_name")
    @Excel(name = "文件名称", width = 15)
    @Schema(description = "文件名称")
    private String fileName;
    /**
     * 数据中心统一编号；字段代码: SeqNo；数据类型: String；长度: 18；暂存必填: 否/否；申报必填: 否
     */
    @TableField("seq_no")
    @Excel(name = "数据中心统一编号", width = 15)
    @Schema(description = "数据中心统一编号")
    private String seqNo;
    /**
     * 进出口标志；字段代码: IEFlag；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否
     */
    @TableField("ie_flag")
    @Excel(name = "进出口标志", width = 15)
    @Schema(description = "进出口标志")
    private String ieFlag;
    /**
     * 申报口岸；字段代码: DeclPort；数据类型: String；长度: 4；暂存必填: 否/否；申报必填: 否
     */
    @TableField("decl_port")
    @Excel(name = "申报口岸", width = 15)
    @Schema(description = "申报口岸")
    private String declPort;
    /**
     * 预计申报日期；字段代码: DDate；数据类型: String；长度: 10；暂存必填: 否/否；申报必填: 否
     */
    @TableField("d_date")
    @Excel(name = "预计申报日期", width = 15)
    @Schema(description = "预计申报日期")
    private String dDate;
    /**
     * 境内收发货人编号；字段代码: TradeCo；数据类型: String；长度: 10；暂存必填: 否/否；申报必填: 否
     */
    @TableField("trade_co")
    @Excel(name = "境内收发货人编号", width = 15)
    @Schema(description = "境内收发货人编号")
    private String tradeCo;
    /**
     * 境内收发货人名称；字段代码: TradeName；数据类型: String；长度: 70；暂存必填: 否/否；申报必填: 否
     */
    @TableField("trade_name")
    @Excel(name = "境内收发货人名称", width = 15)
    @Schema(description = "境内收发货人名称")
    private String tradeName;
    /**
     * 内销比率；字段代码: InRatio；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否
     */
    @TableField("in_ratio")
    @Excel(name = "内销比率", width = 15)
    @Schema(description = "内销比率")
    private BigDecimal inRatio;
    /**
     * 监管方式；字段代码: TradeMode；数据类型: String；长度: 4；暂存必填: 否/否；申报必填: 否
     */
    @TableField("trade_mode")
    @Excel(name = "监管方式", width = 15)
    @Schema(description = "监管方式")
    private String tradeMode;
    /**
     * 征免性质分类；字段代码: CutMode；数据类型: String；长度: 3；暂存必填: 否/否；申报必填: 否
     */
    @TableField("cut_mode")
    @Excel(name = "征免性质分类", width = 15)
    @Schema(description = "征免性质分类")
    private String cutMode;
    /**
     * 成交方式；字段代码: TransMode；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否
     */
    @TableField("trans_mode")
    @Excel(name = "成交方式", width = 15)
    @Schema(description = "成交方式")
    private String transMode;
    /**
     * 运费标记；字段代码: FeeMark；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否
     */
    @TableField("fee_mark")
    @Excel(name = "运费标记", width = 15)
    @Schema(description = "运费标记")
    private String feeMark;
    /**
     * 运费币制；字段代码: FeeCurr；数据类型: String；长度: 3；暂存必填: 否/否；申报必填: 否
     */
    @TableField("fee_curr")
    @Excel(name = "运费币制", width = 15)
    @Schema(description = "运费币制")
    private String feeCurr;
    /**
     * 运费／率；字段代码: FeeRate；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否
     */
    @TableField("fee_rate")
    @Excel(name = "运费／率", width = 15)
    @Schema(description = "运费／率")
    private BigDecimal feeRate;
    /**
     * 保险费标记；字段代码: InsurMark；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否
     */
    @TableField("insur_mark")
    @Excel(name = "保险费标记", width = 15)
    @Schema(description = "保险费标记")
    private String insurMark;
    /**
     * 保险费币制；字段代码: InsurCurr；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否
     */
    @TableField("insur_curr")
    @Excel(name = "保险费币制", width = 15)
    @Schema(description = "保险费币制")
    private String insurCurr;
    /**
     * 保险费／率；字段代码: InsurRate；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否
     */
    @TableField("insur_rate")
    @Excel(name = "保险费／率", width = 15)
    @Schema(description = "保险费／率")
    private BigDecimal insurRate;
    /**
     * 杂费标记；字段代码: OtherMark；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否
     */
    @TableField("other_mark")
    @Excel(name = "杂费标记", width = 15)
    @Schema(description = "杂费标记")
    private String otherMark;
    /**
     * 杂费币制；字段代码: OtherCurr；数据类型: String；长度: 3；暂存必填: 否/否；申报必填: 否
     */
    @TableField("other_curr")
    @Excel(name = "杂费币制", width = 15)
    @Schema(description = "杂费币制")
    private String otherCurr;
    /**
     * 杂费／率；字段代码: OtherRate；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否
     */
    @TableField("other_rate")
    @Excel(name = "杂费／率", width = 15)
    @Schema(description = "杂费／率")
    private BigDecimal otherRate;
    /**
     * 备案号；字段代码: ManualNo；数据类型: String；长度: 12；暂存必填: 否/否；申报必填: 否
     */
    @TableField("manual_no")
    @Excel(name = "备案号", width = 15)
    @Schema(description = "备案号")
    private String manualNo;
    /**
     * 境内收发货人统一代码；字段代码: TradeCoScc；数据类型: String；长度: 18；暂存必填: 否/否；申报必填: 否
     */
    @TableField("trade_co_scc")
    @Excel(name = "境内收发货人统一代码", width = 15)
    @Schema(description = "境内收发货人统一代码")
    private String tradeCoScc;
    /**
     * 毛重；字段代码: GrossWt；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否
     */
    @TableField("gross_wt")
    @Excel(name = "毛重", width = 15)
    @Schema(description = "毛重")
    private BigDecimal grossWt;
    /**
     * 备注；字段代码: NoteS；数据类型: String；长度: 255；暂存必填: 否/否；申报必填: 否
     */
    @TableField("note_s")
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String noteS;
    /**
     * 法律责任；字段代码: LegalLiability；数据类型: String；长度: 255；暂存必填: 否/是；申报必填: 否
     */
    @TableField("legal_liability")
    @Excel(name = "法律责任", width = 15)
    @Schema(description = "法律责任")
    private String legalLiability;
    /**
     * 数字签名信息；字段代码: Sign；数据类型: String；长度: 255；暂存必填: 否/是；申报必填: 否
     */
    @TableField("sign")
    @Excel(name = "数字签名信息", width = 15)
    @Schema(description = "数字签名信息")
    private String sign;
    /**
     * 签名消息号；字段代码: MessId；数据类型: String；长度: 20；暂存必填: 否/否；申报必填: 否
     */
    @TableField("mess_id")
    @Excel(name = "签名消息号", width = 15)
    @Schema(description = "签名消息号")
    private String messId;
    /**
     * 签名证书号；字段代码: CertSeqNo；数据类型: String；长度: 64；暂存必填: 否/是；申报必填: 否
     */
    @TableField("cert_seq_no")
    @Excel(name = "签名证书号", width = 15)
    @Schema(description = "签名证书号")
    private String certSeqNo;
    /**
     * 状态；字段代码: Status；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否
     */
    @TableField("status")
    @Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;
}
