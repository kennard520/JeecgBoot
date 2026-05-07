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
 * 使用人信息表 DecUser。
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
@Schema(description = "使用人信息表 DecUser")
@TableName("DEC_USER")
public class DecUser implements Serializable {
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
     * 使用单位联系人；字段代码: UseOrgPersonCode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 使用单位联系人
     */
    @TableField("use_org_person_code")
    @Excel(name = "使用单位联系人", width = 15)
    @Schema(description = "使用单位联系人")
    private String useOrgPersonCode;
    /**
     * 使用单位联系电话；字段代码: UseOrgPersonTel；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 使用单位联系电话
     */
    @TableField("use_org_person_tel")
    @Excel(name = "使用单位联系电话", width = 15)
    @Schema(description = "使用单位联系电话")
    private String useOrgPersonTel;
}
