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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 许可证VIN信息 DecGoodsLimitVin。
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
@Schema(description = "许可证VIN信息 DecGoodsLimitVin")
@TableName("DEC_GOODS_LIMIT_VIN")
public class DecGoodsLimitVin implements Serializable {
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
     * 许可证信息ID，父表关联ID
     */
    @TableField("dec_goods_limit_id")
    @Excel(name = "许可证信息ID，父表关联ID", width = 15)
    @Schema(description = "许可证信息ID，父表关联ID")
    private Long decGoodsLimitId;
    /**
     * 许可证编号；字段代码: LicenceNo；数据类型: String；长度: 40；暂存必填: 是；申报必填: 是；说明: 许可证编号
     */
    @TableField("licence_no")
    @Excel(name = "许可证编号", width = 15)
    @Schema(description = "许可证编号")
    private String licenceNo;
    /**
     * 许可证类别代码；字段代码: LicTypeCode；数据类型: String；长度: 5；暂存必填: 是；申报必填: 是；说明: 许可证类别代码
     */
    @TableField("lic_type_code")
    @Excel(name = "许可证类别代码", width = 15)
    @Schema(description = "许可证类别代码")
    private String licTypeCode;
    /**
     * VIN序号；字段代码: VinNo；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: VIN序号
     */
    @TableField("vin_no")
    @Excel(name = "VIN序号", width = 15)
    @Schema(description = "VIN序号")
    private String vinNo;
    /**
     * 提/运单日期；字段代码: BillLadDate；数据类型: DATE；长度: 8；暂存必填: 否；申报必填: 否；说明: 提/运单日期 yyyyMMdd
     */
    @TableField("bill_lad_date")
    @Excel(name = "提/运单日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "提/运单日期")
    private Date billLadDate;
    /**
     * 质量保质期；字段代码: QualityQgp；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 质量保质期
     */
    @TableField("quality_qgp")
    @Excel(name = "质量保质期", width = 15)
    @Schema(description = "质量保质期")
    private String qualityQgp;
    /**
     * 发动机号或电机号；字段代码: MotorNo；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 发动机号或电机号
     */
    @TableField("motor_no")
    @Excel(name = "发动机号或电机号", width = 15)
    @Schema(description = "发动机号或电机号")
    private String motorNo;
    /**
     * 车辆识别代码（VIN）；字段代码: VinCode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 车辆识别代码（VIN）
     */
    @TableField("vin_code")
    @Excel(name = "车辆识别代码（VIN）", width = 15)
    @Schema(description = "车辆识别代码（VIN）")
    private String vinCode;
    /**
     * 底盘(车架)号；字段代码: ChassisNo；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 底盘(车架)号
     */
    @TableField("chassis_no")
    @Excel(name = "底盘(车架)号", width = 15)
    @Schema(description = "底盘(车架)号")
    private String chassisNo;
    /**
     * 发票所列数量；字段代码: InvoiceNum；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否；说明: 发票所列数量
     */
    @TableField("invoice_num")
    @Excel(name = "发票所列数量", width = 15)
    @Schema(description = "发票所列数量")
    private BigDecimal invoiceNum;
    /**
     * 品名（中文名称）；字段代码: ProdCnnm；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否；说明: 品名（中文名称）
     */
    @TableField("prod_cnnm")
    @Excel(name = "品名（中文名称）", width = 15)
    @Schema(description = "品名（中文名称）")
    private String prodCnnm;
    /**
     * 品名（英文名称）；字段代码: ProdEnnm；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否；说明: 品名（英文名称）
     */
    @TableField("prod_ennm")
    @Excel(name = "品名（英文名称）", width = 15)
    @Schema(description = "品名（英文名称）")
    private String prodEnnm;
    /**
     * 型号（英文）；字段代码: ModelEn；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否；说明: 型号（英文）
     */
    @TableField("model_en")
    @Excel(name = "型号（英文）", width = 15)
    @Schema(description = "型号（英文）")
    private String modelEn;
    /**
     * 单价；字段代码: PricePerUnit；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 单价
     */
    @TableField("price_per_unit")
    @Excel(name = "单价", width = 15)
    @Schema(description = "单价")
    private String pricePerUnit;
    /**
     * 发票号；字段代码: InvoiceNo；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否
     */
    @TableField("invoice_no")
    @Excel(name = "发票号", width = 15)
    @Schema(description = "发票号")
    private String invoiceNo;
}
