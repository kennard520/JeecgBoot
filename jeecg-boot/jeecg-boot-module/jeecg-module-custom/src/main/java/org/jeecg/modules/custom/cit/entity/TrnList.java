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
 * 进口/出口转关单表体(提单) TrnList。
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
@Schema(description = "进口/出口转关单表体(提单) TrnList")
@TableName("TRN_LIST")
public class TrnList implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，达梦自增标识列
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID，达梦自增标识列", width = 15)
    @Schema(description = "主键ID，达梦自增标识列")
    private Long id;
    /**
     * 转关单表头ID，父表关联ID
     */
    @TableField("trn_head_id")
    @Excel(name = "转关单表头ID，父表关联ID", width = 15)
    @Schema(description = "转关单表头ID，父表关联ID")
    private Long trnHeadId;
    /**
     * 进出境运输方式；字段代码: TrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否
     */
    @TableField("traf_mode")
    @Excel(name = "进出境运输方式", width = 15)
    @Schema(description = "进出境运输方式")
    private String trafMode;
    /**
     * 进出境运输工具编号；字段代码: ShipId；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("ship_id")
    @Excel(name = "进出境运输工具编号", width = 15)
    @Schema(description = "进出境运输工具编号")
    private String shipId;
    /**
     * 进出境运输工具名称；字段代码: ShipNameEn；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否
     */
    @TableField("ship_name_en")
    @Excel(name = "进出境运输工具名称", width = 15)
    @Schema(description = "进出境运输工具名称")
    private String shipNameEn;
    /**
     * 进出境运输工具航次；字段代码: VoyageNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("voyage_no")
    @Excel(name = "进出境运输工具航次", width = 15)
    @Schema(description = "进出境运输工具航次")
    private String voyageNo;
    /**
     * 提单号；字段代码: BillNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("bill_no")
    @Excel(name = "提单号", width = 15)
    @Schema(description = "提单号")
    private String billNo;
    /**
     * 实际进出境日期；字段代码: IEDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd
     */
    @TableField("ie_date")
    @Excel(name = "实际进出境日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "实际进出境日期")
    private Date ieDate;
}
