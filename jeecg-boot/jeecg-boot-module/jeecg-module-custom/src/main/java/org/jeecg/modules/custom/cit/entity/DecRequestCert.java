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
 * 申请单证信息表 DecRequestCert。
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
@Schema(description = "申请单证信息表 DecRequestCert")
@TableName("DEC_REQUEST_CERT")
public class DecRequestCert implements Serializable {
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
     * 申请单证代码；字段代码: AppCertCode；数据类型: String；长度: 50；暂存必填: 是；申报必填: 否
     */
    @TableField("app_cert_code")
    @Excel(name = "申请单证代码", width = 15)
    @Schema(description = "申请单证代码")
    private String appCertCode;
    /**
     * 申请单证正本数；字段代码: ApplOri；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否
     */
    @TableField("appl_ori")
    @Excel(name = "申请单证正本数", width = 15)
    @Schema(description = "申请单证正本数")
    private String applOri;
    /**
     * 申请单证副本数；字段代码: ApplCopyQuan；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否
     */
    @TableField("appl_copy_quan")
    @Excel(name = "申请单证副本数", width = 15)
    @Schema(description = "申请单证副本数")
    private String applCopyQuan;
}
