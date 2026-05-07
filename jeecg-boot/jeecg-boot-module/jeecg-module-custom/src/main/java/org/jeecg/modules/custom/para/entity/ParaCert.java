package org.jeecg.modules.custom.para.entity;

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
 * PARA_CERT ParaCert。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "PARA_CERT")
@TableName("PARA_CERT")
public class ParaCert implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "ID", width = 15)
    @Schema(description = "ID")
    private String id;

    /**
     * CERT_CO；字段名: CERT_CO；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("cert_co")
    @Excel(name = "CERT_CO", width = 15)
    @Schema(description = "CERT_CO")
    private String certCo;

    /**
     * CERT_NAME；字段名: CERT_NAME；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("cert_name")
    @Excel(name = "CERT_NAME", width = 15)
    @Schema(description = "CERT_NAME")
    private String certName;

    /**
     * I_E_MARK；字段名: I_E_MARK；数据类型: VARCHAR2；长度: 1；可空: 是
     */
    @TableField("i_e_mark")
    @Excel(name = "I_E_MARK", width = 15)
    @Schema(description = "I_E_MARK")
    private String iEMark;

    /**
     * REMARK；字段名: REMARK；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("remark")
    @Excel(name = "REMARK", width = 15)
    @Schema(description = "REMARK")
    private String remark;

}
