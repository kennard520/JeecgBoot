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
 * 企业资质信息表 DecCopLimit。
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
@Schema(description = "企业资质信息表 DecCopLimit")
@TableName("DEC_COP_LIMIT")
public class DecCopLimit implements Serializable {
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
     * 企业资质编号；字段代码: EntQualifNo；数据类型: String；长度: 40；暂存必填: 否；申报必填: 否；说明: 根据HS编码设限情况确定
     */
    @TableField("ent_qualif_no")
    @Excel(name = "企业资质编号", width = 15)
    @Schema(description = "企业资质编号")
    private String entQualifNo;
    /**
     * 企业资质类别代码；字段代码: EntQualifTypeCode；数据类型: String；长度: 5；暂存必填: 否；申报必填: 是；说明: 根据HS编码设限情况确定
     */
    @TableField("ent_qualif_type_code")
    @Excel(name = "企业资质类别代码", width = 15)
    @Schema(description = "企业资质类别代码")
    private String entQualifTypeCode;
}
