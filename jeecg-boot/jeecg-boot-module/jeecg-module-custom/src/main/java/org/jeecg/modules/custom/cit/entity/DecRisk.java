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
 * 风险评估信息DecRisk。
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
@Schema(description = "风险评估信息DecRisk")
@TableName("DEC_RISK")
public class DecRisk implements Serializable {
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
     * 风险评估结果；字段代码: Risk；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否
     */
    @TableField("risk")
    @Excel(name = "风险评估结果", width = 15)
    @Schema(description = "风险评估结果")
    private String risk;
    /**
     * 数字签名信息；字段代码: Sign；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否
     */
    @TableField("sign")
    @Excel(name = "数字签名信息", width = 15)
    @Schema(description = "数字签名信息")
    private String sign;
    /**
     * 处理日期；字段代码: SignDate；数据类型: DateTime；长度: 16；暂存必填: 否；申报必填: 否
     */
    @TableField("sign_date")
    @Excel(name = "处理日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理日期")
    private Date signDate;
    /**
     * 备注；字段代码: Note；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否
     */
    @TableField("note")
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String note;
}
