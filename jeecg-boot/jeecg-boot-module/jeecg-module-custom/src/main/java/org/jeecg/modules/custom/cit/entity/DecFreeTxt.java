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

/**
 * 报关单自由文本信息DecFreeTxt。
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
@Schema(description = "报关单自由文本信息DecFreeTxt")
@TableName("DEC_FREE_TXT")
public class DecFreeTxt implements Serializable {
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
     * 监管仓号；字段代码: BonNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否；说明: 空值
     */
    @TableField("bon_no")
    @Excel(name = "监管仓号", width = 15)
    @Schema(description = "监管仓号")
    private String bonNo;
    /**
     * 货场代码；字段代码: CusFie；数据类型: String；长度: 8；暂存必填: 否；申报必填: 否；说明: 空值
     */
    @TableField("cus_fie")
    @Excel(name = "货场代码", width = 15)
    @Schema(description = "货场代码")
    private String cusFie;
    /**
     * 报关员联系方式；字段代码: DecBpNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("dec_bp_no")
    @Excel(name = "报关员联系方式", width = 15)
    @Schema(description = "报关员联系方式")
    private String decBpNo;
    /**
     * 申报人员证号；字段代码: DecNo；数据类型: String；长度: 13；暂存必填: 否；申报必填: 否；说明: 报关员海关注册编码
     */
    @TableField("dec_no")
    @Excel(name = "申报人员证号", width = 15)
    @Schema(description = "申报人员证号")
    private String decNo;
    /**
     * 关联报关单号；字段代码: RelId；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 空值
     */
    @TableField("rel_id")
    @Excel(name = "关联报关单号", width = 15)
    @Schema(description = "关联报关单号")
    private String relId;
    /**
     * 关联备案号；字段代码: RelManNo；数据类型: String；长度: 12；暂存必填: 否；申报必填: 否；说明: 空值
     */
    @TableField("rel_man_no")
    @Excel(name = "关联备案号", width = 15)
    @Schema(description = "关联备案号")
    private String relManNo;
    /**
     * 航次号；字段代码: VoyNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("voy_no")
    @Excel(name = "航次号", width = 15)
    @Schema(description = "航次号")
    private String voyNo;
}
