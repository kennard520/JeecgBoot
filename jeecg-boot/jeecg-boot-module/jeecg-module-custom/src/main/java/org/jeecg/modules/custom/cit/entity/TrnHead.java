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
 * 进口/出口转关单表头 TrnHead。
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
@Schema(description = "进口/出口转关单表头 TrnHead")
@TableName("TRN_HEAD")
public class TrnHead implements Serializable {
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
     * 转关单统一编号；字段代码: TrnPreId；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 唯一标识一票报关单中转关提前报关单。首次导入传空值，由系统自动生成并返回客户端。
     */
    @TableField("trn_pre_id")
    @Excel(name = "转关单统一编号", width = 15)
    @Schema(description = "转关单统一编号")
    private String trnPreId;
    /**
     * 载货清单号；字段代码: TransNo；数据类型: String；长度: 16；暂存必填: 否；申报必填: 否
     */
    @TableField("trans_no")
    @Excel(name = "载货清单号", width = 15)
    @Schema(description = "载货清单号")
    private String transNo;
    /**
     * 转关申报单号；字段代码: TurnNo；数据类型: String；长度: 16；暂存必填: 否；申报必填: 否
     */
    @TableField("turn_no")
    @Excel(name = "转关申报单号", width = 15)
    @Schema(description = "转关申报单号")
    private String turnNo;
    /**
     * 境内运输方式；字段代码: NativeTrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("native_traf_mode")
    @Excel(name = "境内运输方式", width = 15)
    @Schema(description = "境内运输方式")
    private String nativeTrafMode;
    /**
     * 境内运输工具编号；字段代码: TrafCustomsNo；数据类型: String；长度: 13；暂存必填: 否；申报必填: 否
     */
    @TableField("traf_customs_no")
    @Excel(name = "境内运输工具编号", width = 15)
    @Schema(description = "境内运输工具编号")
    private String trafCustomsNo;
    /**
     * 境内运输工具名称；字段代码: NativeShipName；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("native_ship_name")
    @Excel(name = "境内运输工具名称", width = 15)
    @Schema(description = "境内运输工具名称")
    private String nativeShipName;
    /**
     * 境内运输工具航次；字段代码: NativeVoyageNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("native_voyage_no")
    @Excel(name = "境内运输工具航次", width = 15)
    @Schema(description = "境内运输工具航次")
    private String nativeVoyageNo;
    /**
     * 承运单位名称；字段代码: ContractorName；数据类型: String；长度: 70；暂存必填: 否；申报必填: 否
     */
    @TableField("contractor_name")
    @Excel(name = "承运单位名称", width = 15)
    @Schema(description = "承运单位名称")
    private String contractorName;
    /**
     * 承运单位组织机构代码；字段代码: ContractorCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否
     */
    @TableField("contractor_code")
    @Excel(name = "承运单位组织机构代码", width = 15)
    @Schema(description = "承运单位组织机构代码")
    private String contractorCode;
    /**
     * 转关类型；字段代码: TransFlag；数据类型: String；长度: 2；暂存必填: 否；申报必填: 否；说明: 转关类型：1—转关提前 AA：出口二次转关 1G：提前/工厂验放 1T：提前/暂时进出口 1E：提前/中欧班列 1P：提前/市场采购出口 1K：提前/出口空运联程
     */
    @TableField("trans_flag")
    @Excel(name = "转关类型", width = 15)
    @Schema(description = "转关类型")
    private String transFlag;
    /**
     * 预计运抵指运地时间；字段代码: ValidTime；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd
     */
    @TableField("valid_time")
    @Excel(name = "预计运抵指运地时间", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "预计运抵指运地时间")
    private Date validTime;
    /**
     * 是否启用电子关锁标志；字段代码: ESealFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: Y：是 N：否
     */
    @TableField("e_seal_flag")
    @Excel(name = "是否启用电子关锁标志", width = 15)
    @Schema(description = "是否启用电子关锁标志")
    private String eSealFlag;
    /**
     * 备注；字段代码: Notes；数据类型: String；长度: 200；暂存必填: 否；申报必填: 否
     */
    @TableField("notes")
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String notes;
    /**
     * 转关单类型；字段代码: TrnType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 1：无纸申报 0或空：普通有纸申报
     */
    @TableField("trn_type")
    @Excel(name = "转关单类型", width = 15)
    @Schema(description = "转关单类型")
    private String trnType;
    /**
     * 转关申报单位统一代码；字段代码: ApplCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否
     */
    @TableField("appl_code_scc")
    @Excel(name = "转关申报单位统一代码", width = 15)
    @Schema(description = "转关申报单位统一代码")
    private String applCodeScc;
}
