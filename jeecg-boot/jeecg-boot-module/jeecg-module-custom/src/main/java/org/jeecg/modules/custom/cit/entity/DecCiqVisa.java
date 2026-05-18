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
 * 检验检疫签证申报要素 DecCiqVisa。
 *
 * <p>用于保存单一窗口检验检疫签证申报要素弹窗中的证书和签证补充信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "检验检疫签证申报要素")
@TableName("DEC_CIQ_VISA")
public class DecCiqVisa implements Serializable {
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
    @Excel(name = "报关单表头ID", width = 15)
    @Schema(description = "报关单表头ID")
    private Long decHeadId;

    /**
     * 报关单商品表体ID，父表关联ID
     */
    @TableField("dec_list_id")
    @Excel(name = "报关单商品表体ID", width = 15)
    @Schema(description = "报关单商品表体ID")
    private Long decListId;

    /**
     * 证书代码
     */
    @TableField("cert_code")
    @Excel(name = "证书代码", width = 15)
    @Schema(description = "证书代码")
    private String certCode;

    /**
     * 证书名称
     */
    @TableField("cert_name")
    @Excel(name = "证书名称", width = 15)
    @Schema(description = "证书名称")
    private String certName;

    /**
     * 正本数量
     */
    @TableField("cert_original_count")
    @Excel(name = "正本数量", width = 15)
    @Schema(description = "正本数量")
    private String certOriginalCount;

    /**
     * 副本数量
     */
    @TableField("cert_copy_count")
    @Excel(name = "副本数量", width = 15)
    @Schema(description = "副本数量")
    private String certCopyCount;

    /**
     * 境内收发货人名称（外文）
     */
    @TableField("domestic_consignee_ename")
    @Excel(name = "境内收发货人名称（外文）", width = 15)
    @Schema(description = "境内收发货人名称（外文）")
    private String domesticConsigneeEname;

    /**
     * 境外收发货人名称（中文）
     */
    @TableField("overseas_consignor_cname")
    @Excel(name = "境外收发货人名称（中文）", width = 15)
    @Schema(description = "境外收发货人名称（中文）")
    private String overseasConsignorCname;

    /**
     * 境外发货人地址
     */
    @TableField("overseas_consignor_addr")
    @Excel(name = "境外发货人地址", width = 15)
    @Schema(description = "境外发货人地址")
    private String overseasConsignorAddr;

    /**
     * 卸毕日期
     */
    @TableField("cmpl_dschrg_dt")
    @Excel(name = "卸毕日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "卸毕日期")
    private Date cmplDschrgDt;

    /**
     * 商品英文名称
     */
    @TableField("decl_goods_ename")
    @Excel(name = "商品英文名称", width = 15)
    @Schema(description = "商品英文名称")
    private String declGoodsEname;
}
