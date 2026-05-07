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
 * 特许权使用费申请表DecRoyaltyFee。
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
@Schema(description = "特许权使用费申请表DecRoyaltyFee")
@TableName("DEC_ROYALTY_FEE")
public class DecRoyaltyFee implements Serializable {
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
     * 价格预裁定编号；字段代码: PricePreDeterminNo；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 是否经过海关价格预裁定选择是时必须填写，需要进行长度审核，同预裁定系统的编号要求。
     */
    @TableField("price_pre_determin_no")
    @Excel(name = "价格预裁定编号", width = 15)
    @Schema(description = "价格预裁定编号")
    private String pricePreDeterminNo;
    /**
     * 应税特许权使用费申报情形；字段代码: TaxRoyaltyDeclType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 0：首次申报 1：延续性申报
     */
    @TableField("tax_royalty_decl_type")
    @Excel(name = "应税特许权使用费申报情形", width = 15)
    @Schema(description = "应税特许权使用费申报情形")
    private String taxRoyaltyDeclType;
    /**
     * 合同/协议号；字段代码: ContractNo；数据类型: String；长度: 500；暂存必填: 是；申报必填: 是
     */
    @TableField("contract_no")
    @Excel(name = "合同/协议号", width = 15)
    @Schema(description = "合同/协议号")
    private String contractNo;
    /**
     * 授权方；字段代码: Authorizer；数据类型: String；长度: 500；暂存必填: 是；申报必填: 是
     */
    @TableField("authorizer")
    @Excel(name = "授权方", width = 15)
    @Schema(description = "授权方")
    private String authorizer;
    /**
     * 被授权方；字段代码: AuthorizedPerson；数据类型: String；长度: 500；暂存必填: 是；申报必填: 是
     */
    @TableField("authorized_person")
    @Excel(name = "被授权方", width = 15)
    @Schema(description = "被授权方")
    private String authorizedPerson;
    /**
     * 支付方式；字段代码: PayType；数据类型: String；长度: 20；暂存必填: 是；申报必填: 是；说明: 0：一次性支付 1：定期支付 2：混合
     */
    @TableField("pay_type")
    @Excel(name = "支付方式", width = 15)
    @Schema(description = "支付方式")
    private String payType;
    /**
     * 支付时间；字段代码: PayTime；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是；说明: 年月日 yyyy-MM-dd
     */
    @TableField("pay_time")
    @Excel(name = "支付时间", width = 15)
    @Schema(description = "支付时间")
    private String payTime;
    /**
     * 支付计提周期；字段代码: PayPeriod；数据类型: Integer；长度: 2；暂存必填: 否；申报必填: 否；说明: 按月来计算
     */
    @TableField("pay_period")
    @Excel(name = "支付计提周期", width = 15)
    @Schema(description = "支付计提周期")
    private Integer payPeriod;
    /**
     * 合同/协议起始执行时间；字段代码: EffectiveDateTime；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是；说明: 年月日 yyyy-MM-dd
     */
    @TableField("effective_date_time")
    @Excel(name = "合同/协议起始执行时间", width = 15)
    @Schema(description = "合同/协议起始执行时间")
    private String effectiveDateTime;
    /**
     * 合同协议终止时间；字段代码: ExpirationDateTime；数据类型: DATE；长度: 10；暂存必填: 是；申报必填: 是；说明: 如果合同未明确规定有效时间或有效时间为长期，录入终止时间为10年，年月日.格式：yyyy-MM-dd
     */
    @TableField("expiration_date_time")
    @Excel(name = "合同协议终止时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "合同协议终止时间")
    private Date expirationDateTime;
    /**
     * 特许权使用费金额；字段代码: RoyaltyAmount；数据类型: Decimal；长度: 19,2；暂存必填: 是；申报必填: 是；说明: 小数点后2位
     */
    @TableField("royalty_amount")
    @Excel(name = "特许权使用费金额", width = 15)
    @Schema(description = "特许权使用费金额")
    private BigDecimal royaltyAmount;
    /**
     * 币制；字段代码: Curr；数据类型: String；长度: 3；暂存必填: 是；申报必填: 是
     */
    @TableField("curr")
    @Excel(name = "币制", width = 15)
    @Schema(description = "币制")
    private String curr;
    /**
     * 特许权使用费类型；字段代码: RoyaltyFeeType；数据类型: String；长度: 5；暂存必填: 是；申报必填: 是；说明: 按位存放 第一位:专利权或者专有技术使用权 第二位:商标权 第三位:著作权 第四位:分销权、销售权或者其他类似权利 (0代表没有勾选,1代表勾选)
     */
    @TableField("royalty_fee_type")
    @Excel(name = "特许权使用费类型", width = 15)
    @Schema(description = "特许权使用费类型")
    private String royaltyFeeType;
    /**
     * 随附材料清单类型；字段代码: EdocType；数据类型: String；长度: 8；暂存必填: 是；申报必填: 是；说明: 按位存放 第一位:特许权使用费涉及的原进口货物报关单号 第二位:特许权使用费合同/协议 第三位:特许权使用费发票 第四位:特许权使用费支付凭证 第五位:代扣代缴税纳税凭证 第六位:特许权使用费其他 (0代表没有勾选,1代表勾选)
     */
    @TableField("edoc_type")
    @Excel(name = "随附材料清单类型", width = 15)
    @Schema(description = "随附材料清单类型")
    private String edocType;
    /**
     * 说明；字段代码: Statment；数据类型: String；长度: 2500；暂存必填: 是；申报必填: 是
     */
    @TableField("statment")
    @Excel(name = "说明", width = 15)
    @Schema(description = "说明")
    private String statment;
    /**
     * 是否保密；字段代码: IsSecret；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 是否保密：1：是、0：否
     */
    @TableField("is_secret")
    @Excel(name = "是否保密", width = 15)
    @Schema(description = "是否保密")
    private String isSecret;
    /**
     * 是否经过海关审核认定；字段代码: IsCusAudit；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 是否经过海关审核认定： 1：是、0：否
     */
    @TableField("is_cus_audit")
    @Excel(name = "是否经过海关审核认定", width = 15)
    @Schema(description = "是否经过海关审核认定")
    private String isCusAudit;
    /**
     * 是否经过海关价格预裁定；字段代码: IsCusPricePreDetermin；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 是否经过海关价格预裁定： 1：是、0：否
     */
    @TableField("is_cus_price_pre_determin")
    @Excel(name = "是否经过海关价格预裁定", width = 15)
    @Schema(description = "是否经过海关价格预裁定")
    private String isCusPricePreDetermin;
    /**
     * 合同/协议签约时间；字段代码: IssueDateTime；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是；说明: 年月日 yyyy-MM-dd
     */
    @TableField("issue_date_time")
    @Excel(name = "合同/协议签约时间", width = 15)
    @Schema(description = "合同/协议签约时间")
    private String issueDateTime;
    /**
     * 本次支付对应的计提周期起始时间；字段代码: PeriodStartDate；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 年月日 yyyy-MM-dd
     */
    @TableField("period_start_date")
    @Excel(name = "本次支付对应的计提周期起始时间", width = 15)
    @Schema(description = "本次支付对应的计提周期起始时间")
    private String periodStartDate;
    /**
     * 本次支付对应的计提周期终止时间；字段代码: PeriodEndDate；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 年月日 yyyy-MM-dd
     */
    @TableField("period_end_date")
    @Excel(name = "本次支付对应的计提周期终止时间", width = 15)
    @Schema(description = "本次支付对应的计提周期终止时间")
    private String periodEndDate;
}
