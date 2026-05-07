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
import java.util.Date;

/**
 * 报关单签名DecSign。
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
@Schema(description = "报关单签名DecSign")
@TableName("DEC_SIGN")
public class DecSign implements Serializable {
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
     * 操作类型；字段代码: OperType；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 操作类型 A：报关单上载 B：报关单、转关单上载 C：报关单申报 D：报关单、转关单申报 E：电子手册报关单上载（此种操作类型的报关单上载时不作非空和逻辑校验） G：报关单暂存（转关提前报关单暂存）
     */
    @TableField("oper_type")
    @Excel(name = "操作类型", width = 15)
    @Schema(description = "操作类型")
    private String operType;
    /**
     * 操作员IC卡号；字段代码: ICCode；数据类型: String；长度: 13；暂存必填: 否；申报必填: 否；说明: 当前操作的操作员IC卡号。 对应于2.0版接口： <ICCARD_ID>签名人IC卡ID</ICCARD_ID>；对应表头中的TypistNo
     */
    @TableField("ic_code")
    @Excel(name = "操作员IC卡号", width = 15)
    @Schema(description = "操作员IC卡号")
    private String icCode;
    /**
     * 操作员姓名；字段代码: OperName；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否；说明: 当前操作的操作员姓名
     */
    @TableField("oper_name")
    @Excel(name = "操作员姓名", width = 15)
    @Schema(description = "操作员姓名")
    private String operName;
    /**
     * 操作企业组织机构代码；字段代码: CopCode；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 操作企业组织机构代码，对应表头中的CopCode
     */
    @TableField("cop_code")
    @Excel(name = "操作企业组织机构代码", width = 15)
    @Schema(description = "操作企业组织机构代码")
    private String copCode;
    /**
     * 客户端报关单编号；字段代码: ClientSeqNo；数据类型: String；长度: 18；暂存必填: 是；申报必填: 是；说明: 客户端自行编制的编号，唯一识别一票报关单。
     */
    @TableField("client_seq_no")
    @Excel(name = "客户端报关单编号", width = 15)
    @Schema(description = "客户端报关单编号")
    private String clientSeqNo;
    /**
     * 数字签名信息；字段代码: Sign；数据类型: String；长度: 200；暂存必填: 否；申报必填: 是
     */
    @TableField("sign")
    @Excel(name = "数字签名信息", width = 15)
    @Schema(description = "数字签名信息")
    private String sign;
    /**
     * 签名日期；字段代码: SignDate；数据类型: DateTime；长度: 16；暂存必填: 否；申报必填: 是
     */
    @TableField("sign_date")
    @Excel(name = "签名日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "签名日期")
    private Date signDate;
    /**
     * 客户端邮箱的HostId；字段代码: HostId；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用。
     */
    @TableField("host_id")
    @Excel(name = "客户端邮箱的HostId", width = 15)
    @Schema(description = "客户端邮箱的HostId")
    private String hostId;
    /**
     * 操作员卡的证书号；字段代码: Certificate；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 所有操作都需求取卡信息进行权限校验，所以所有导入都必须填写该字段。
     */
    @TableField("certificate")
    @Excel(name = "操作员卡的证书号", width = 15)
    @Schema(description = "操作员卡的证书号")
    private String certificate;
    /**
     * 签名人分类；字段代码: DomainId；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 1：录入人 2：申报人
     */
    @TableField("domain_id")
    @Excel(name = "签名人分类", width = 15)
    @Schema(description = "签名人分类")
    private String domainId;
    /**
     * 备注；字段代码: Note；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否
     */
    @TableField("note")
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String note;
    /**
     * 对应清单统一编号；字段代码: BillSeqNo；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 如果报关单有对应的清单填报关单对应的清单统一编号；如果没有清单填企业自编统一编号（对每个邮箱要唯一）
     */
    @TableField("bill_seq_no")
    @Excel(name = "对应清单统一编号", width = 15)
    @Schema(description = "对应清单统一编号")
    private String billSeqNo;
}
