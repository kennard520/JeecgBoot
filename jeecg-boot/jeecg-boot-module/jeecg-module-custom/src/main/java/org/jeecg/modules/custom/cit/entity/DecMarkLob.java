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
 * 标记号码附件表DecMarkLob。
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
@Schema(description = "标记号码附件表DecMarkLob")
@TableName("DEC_MARK_LOB")
public class DecMarkLob implements Serializable {
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
     * 附件名称；字段代码: AttachName；数据类型: String；长度: 80；暂存必填: 是；申报必填: 是；说明: 附件名称
     */
    @TableField("attach_name")
    @Excel(name = "附件名称", width = 15)
    @Schema(description = "附件名称")
    private String attachName;
    /**
     * 附件类型；字段代码: AttachType；数据类型: String；长度: 2；暂存必填: 否；申报必填: 否；说明: 附件类型
     */
    @TableField("attach_type")
    @Excel(name = "附件类型", width = 15)
    @Schema(description = "附件类型")
    private String attachType;
    /**
     * 附件；字段代码: Attachment；数据类型: BLOB；暂存必填: 是；申报必填: 是；说明: 附件（计算机无法录入的标记及号码的图案或内容，不超过1MB）。 需对附件进行BASE64编码。
     */
    @TableField("attachment")
    @Schema(description = "附件")
    private byte[] attachment;
}
