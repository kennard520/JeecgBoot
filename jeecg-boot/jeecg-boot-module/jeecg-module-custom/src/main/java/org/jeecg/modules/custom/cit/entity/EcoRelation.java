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
 * 随附单证对应关系EcoRelation。
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
@Schema(description = "随附单证对应关系EcoRelation")
@TableName("ECO_RELATION")
public class EcoRelation implements Serializable {
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
     * 报关单商品表体ID，父表关联ID
     */
    @TableField("dec_list_id")
    @Excel(name = "报关单商品表体ID，父表关联ID", width = 15)
    @Schema(description = "报关单商品表体ID，父表关联ID")
    private Long decListId;
    /**
     * 单证代码；字段代码: CertType；数据类型: String；长度: 3；暂存必填: 是/是；申报必填: 否；说明: 1、当导入优惠贸易协定享惠信息中的原产地证明商品项号关联关系时，单证代码填写格式为：Y+优惠贸易协定代码。样例:Y03。2、当导入其他随附单证商品项号关联关系时，单证代码填写随附单证代码。
     */
    @TableField("cert_type")
    @Excel(name = "单证代码", width = 15)
    @Schema(description = "单证代码")
    private String certType;
    /**
     * 报关单商品项号；字段代码: DecGNo；数据类型: String；长度: 9；暂存必填: 是/是；申报必填: 否
     */
    @TableField("dec_g_no")
    @Excel(name = "报关单商品项号", width = 15)
    @Schema(description = "报关单商品项号")
    private String decGNo;
    /**
     * 单证编号；字段代码: EcoCertNo；数据类型: String；长度: 64；暂存必填: 是/是；申报必填: 否；说明: 当导入优惠贸易协定享惠信息中的原产地证明商品项号关联关系时，单证编号填写原产地证明类型（C/D）+原产地证明编号；如原产地证明类型为空，则仅填写原产地证明编号。样例：C1234567890。
     */
    @TableField("eco_cert_no")
    @Excel(name = "单证编号", width = 15)
    @Schema(description = "单证编号")
    private String ecoCertNo;
    /**
     * 对应随附单证商品项号；字段代码: EcoGNo；数据类型: String；长度: 9；暂存必填: 是/是；申报必填: 否
     */
    @TableField("eco_g_no")
    @Excel(name = "对应随附单证商品项号", width = 15)
    @Schema(description = "对应随附单证商品项号")
    private String ecoGNo;
    /**
     * 扩展字段；字段代码: ExtendFiled；数据类型: String；长度: 255；暂存必填: 否/否；申报必填: 否
     */
    @TableField("extend_filed")
    @Excel(name = "扩展字段", width = 15)
    @Schema(description = "扩展字段")
    private String extendFiled;
}
