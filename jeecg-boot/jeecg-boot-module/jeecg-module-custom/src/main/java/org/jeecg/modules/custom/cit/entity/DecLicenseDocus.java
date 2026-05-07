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
 * 随附单证 DecLicenseDocus。
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
@Schema(description = "随附单证 DecLicenseDocus")
@TableName("DEC_LICENSE_DOCUS")
public class DecLicenseDocus implements Serializable {
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
     * 单证代码；字段代码: DocuCode；数据类型: String；长度: 1；暂存必填: 是；申报必填: 否
     */
    @TableField("docu_code")
    @Excel(name = "单证代码", width = 15)
    @Schema(description = "单证代码")
    private String docuCode;
    /**
     * 单证编号；字段代码: CertCode；数据类型: String；长度: 32；暂存必填: 是；申报必填: 否
     */
    @TableField("cert_code")
    @Excel(name = "单证编号", width = 15)
    @Schema(description = "单证编号")
    private String certCode;
}
