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
 * 两段准入申请DecTpAccess。
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
@Schema(description = "两段准入申请DecTpAccess")
@TableName("DEC_TP_ACCESS")
public class DecTpAccess implements Serializable {
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
     * 转场申请；字段代码: TransitionApply；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空/0：未勾选 1：勾选
     */
    @TableField("transition_apply")
    @Excel(name = "转场申请", width = 15)
    @Schema(description = "转场申请")
    private String transitionApply;
    /**
     * 转入场所场地；字段代码: TransitionSite；数据类型: String；长度: 11；暂存必填: 否；申报必填: 否；说明: 填写监管场地海关编码； 仅在勾选了“转场申请”时必填。
     */
    @TableField("transition_site")
    @Excel(name = "转入场所场地", width = 15)
    @Schema(description = "转入场所场地")
    private String transitionSite;
    /**
     * 附条件提离申请；字段代码: ConditionalLiftoffApply；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空/0：未勾选 1：勾选
     */
    @TableField("conditional_liftoff_apply")
    @Excel(name = "附条件提离申请", width = 15)
    @Schema(description = "附条件提离申请")
    private String conditionalLiftoffApply;
    /**
     * 口岸与目的地合并检查申请；字段代码: PortDestMergeCheckApply；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空/0：未勾选 1：勾选
     */
    @TableField("port_dest_merge_check_apply")
    @Excel(name = "口岸与目的地合并检查申请", width = 15)
    @Schema(description = "口岸与目的地合并检查申请")
    private String portDestMergeCheckApply;
}
