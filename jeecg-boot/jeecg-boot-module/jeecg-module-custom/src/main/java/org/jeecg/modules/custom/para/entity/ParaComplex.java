package org.jeecg.modules.custom.para.entity;

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
 * 商品编码 ParaComplex。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "商品编码")
@TableName("PARA_COMPLEX")
public class ParaComplex implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 256；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 商品编码；字段名: CODE_T；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("code_t")
    @Excel(name = "商品编码", width = 15)
    @Schema(description = "商品编码")
    private String codeT;

    /**
     * 附加码；字段名: CODE_S；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("code_s")
    @Excel(name = "附加码", width = 15)
    @Schema(description = "附加码")
    private String codeS;

    /**
     * 商品编号全称；字段名: CODE_T_S；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("code_t_s")
    @Excel(name = "商品编号全称", width = 15)
    @Schema(description = "商品编号全称")
    private String codeTS;

    /**
     * 商品名称；字段名: G_NAME；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("g_name")
    @Excel(name = "商品名称", width = 15)
    @Schema(description = "商品名称")
    private String gName;

    /**
     * 最惠国进口税率；字段名: LOW_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("low_rate")
    @Excel(name = "最惠国进口税率", width = 15)
    @Schema(description = "最惠国进口税率")
    private BigDecimal lowRate;

    /**
     * 普通进口税率；字段名: HIGH_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("high_rate")
    @Excel(name = "普通进口税率", width = 15)
    @Schema(description = "普通进口税率")
    private BigDecimal highRate;

    /**
     * 出口从价关税率；字段名: OUT_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("out_rate")
    @Excel(name = "出口从价关税率", width = 15)
    @Schema(description = "出口从价关税率")
    private BigDecimal outRate;

    /**
     * 消费税计征标准；字段名: REG_MARK；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("reg_mark")
    @Excel(name = "消费税计征标准", width = 15)
    @Schema(description = "消费税计征标准")
    private String regMark;

    /**
     * 进口从价消费税税率；字段名: REG_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("reg_rate")
    @Excel(name = "进口从价消费税税率", width = 15)
    @Schema(description = "进口从价消费税税率")
    private BigDecimal regRate;

    /**
     * 进口增值税计征标准；字段名: TAX_TYPE；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("tax_type")
    @Excel(name = "进口增值税计征标准", width = 15)
    @Schema(description = "进口增值税计征标准")
    private String taxType;

    /**
     * 增值税率；字段名: TAX_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("tax_rate")
    @Excel(name = "增值税率", width = 15)
    @Schema(description = "增值税率")
    private BigDecimal taxRate;

    /**
     * 通用税率；字段名: COMM_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("comm_rate")
    @Excel(name = "通用税率", width = 15)
    @Schema(description = "通用税率")
    private BigDecimal commRate;

    /**
     * 对台调节税率；字段名: TAIWAN_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("taiwan_rate")
    @Excel(name = "对台调节税率", width = 15)
    @Schema(description = "对台调节税率")
    private BigDecimal taiwanRate;

    /**
     * 限制类商品标志；字段名: OTHER_TYPE；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("other_type")
    @Excel(name = "限制类商品标志", width = 15)
    @Schema(description = "限制类商品标志")
    private String otherType;

    /**
     * 杂费率；字段名: OTHER_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("other_rate")
    @Excel(name = "杂费率", width = 15)
    @Schema(description = "杂费率")
    private BigDecimal otherRate;

    /**
     * 法一单位；字段名: UNIT_1；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("unit_1")
    @Excel(name = "法一单位", width = 15)
    @Schema(description = "法一单位")
    private String unit1;

    /**
     * 法二单位；字段名: UNIT_2；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("unit_2")
    @Excel(name = "法二单位", width = 15)
    @Schema(description = "法二单位")
    private String unit2;

    /**
     * 最低进口价；字段名: ILOW_PRICE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("ilow_price")
    @Excel(name = "最低进口价", width = 15)
    @Schema(description = "最低进口价")
    private BigDecimal ilowPrice;

    /**
     * 最高进口价；字段名: IHIGH_PRICE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("ihigh_price")
    @Excel(name = "最高进口价", width = 15)
    @Schema(description = "最高进口价")
    private BigDecimal ihighPrice;

    /**
     * 最低出口价；字段名: ELOW_PRICE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("elow_price")
    @Excel(name = "最低出口价", width = 15)
    @Schema(description = "最低出口价")
    private BigDecimal elowPrice;

    /**
     * 最高出口价；字段名: EHIGH_PRICE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("ehigh_price")
    @Excel(name = "最高出口价", width = 15)
    @Schema(description = "最高出口价")
    private BigDecimal ehighPrice;

    /**
     * 最大进口量；字段名: MAX_IN；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("max_in")
    @Excel(name = "最大进口量", width = 15)
    @Schema(description = "最大进口量")
    private BigDecimal maxIn;

    /**
     * 最大出口量；字段名: MAX_OUT；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("max_out")
    @Excel(name = "最大出口量", width = 15)
    @Schema(description = "最大出口量")
    private BigDecimal maxOut;

    /**
     * 监管条件；字段名: CONTROL_MA；数据类型: VARCHAR2；长度: 64；可空: 是
     */
    @TableField("control_ma")
    @Excel(name = "监管条件", width = 15)
    @Schema(description = "监管条件")
    private String controlMa;

    /**
     * 重点审价标记；字段名: CHK_PRICE；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("chk_price")
    @Excel(name = "重点审价标记", width = 15)
    @Schema(description = "重点审价标记")
    private String chkPrice;

    /**
     * 征税要求标记；字段名: TARIFF_MARK；数据类型: VARCHAR2；长度: 16；可空: 是
     */
    @TableField("tariff_mark")
    @Excel(name = "征税要求标记", width = 15)
    @Schema(description = "征税要求标记")
    private String tariffMark;

    /**
     * 备注；字段名: NOTE_S；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("note_s")
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String noteS;

    /**
     * 排序码；字段名: PK_SEQ；数据类型: DECIMAL；长度: 22；精度: 8；可空: 是
     */
    @TableField("pk_seq")
    @Excel(name = "排序码", width = 15)
    @Schema(description = "排序码")
    private Integer pkSeq;

    /**
     * 反倾销标识；字段名: ANTI_MARK；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("anti_mark")
    @Excel(name = "反倾销标识", width = 15)
    @Schema(description = "反倾销标识")
    private String antiMark;

    /**
     * 反倾销税率；字段名: ANTI_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("anti_rate")
    @Excel(name = "反倾销税率", width = 15)
    @Schema(description = "反倾销税率")
    private BigDecimal antiRate;

    /**
     * 申报要素；字段名: ELEMENT；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("element")
    @Excel(name = "申报要素", width = 15)
    @Schema(description = "申报要素")
    private String element;

    /**
     * 所属年月；字段名: YEAR；数据类型: VARCHAR2；长度: 120；可空: 是
     */
    @TableField("year")
    @Excel(name = "所属年月", width = 15)
    @Schema(description = "所属年月")
    private String year;

    /**
     * 暂定进口税率；字段名: TEMP_RATE；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("temp_rate")
    @Excel(name = "暂定进口税率", width = 15)
    @Schema(description = "暂定进口税率")
    private BigDecimal tempRate;

    /**
     * 出口退税率；字段名: REBATES；数据类型: DECIMAL；长度: 22；精度: 19；小数位: 5；可空: 是
     */
    @TableField("rebates")
    @Excel(name = "出口退税率", width = 15)
    @Schema(description = "出口退税率")
    private BigDecimal rebates;

    /**
     * 检验检疫类别；字段名: CIQ_TYPE；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("ciq_type")
    @Excel(name = "检验检疫类别", width = 15)
    @Schema(description = "检验检疫类别")
    private String ciqType;

    /**
     * 申报计量单位(口岸)；字段名: PORT_UNIT；数据类型: VARCHAR2；长度: 12；可空: 是
     */
    @TableField("port_unit")
    @Excel(name = "申报计量单位(口岸)", width = 15)
    @Schema(description = "申报计量单位(口岸)")
    private String portUnit;

    /**
     * 申报计量单位(保税区)；字段名: ZONE_UNIT；数据类型: VARCHAR2；长度: 12；可空: 是
     */
    @TableField("zone_unit")
    @Excel(name = "申报计量单位(保税区)", width = 15)
    @Schema(description = "申报计量单位(保税区)")
    private String zoneUnit;

    /**
     * 独立报关标志；字段名: SPECIAL_MARK；数据类型: VARCHAR2；长度: 4；可空: 是
     */
    @TableField("special_mark")
    @Excel(name = "独立报关标志", width = 15)
    @Schema(description = "独立报关标志")
    private String specialMark;

    /**
     * 能效说明；字段名: EFFICACY；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("efficacy")
    @Excel(name = "能效说明", width = 15)
    @Schema(description = "能效说明")
    private String efficacy;

    /**
     * 能效标识；字段名: ENERGY_MARK；数据类型: VARCHAR2；长度: 2048；可空: 是
     */
    @TableField("energy_mark")
    @Excel(name = "能效标识", width = 15)
    @Schema(description = "能效标识")
    private String energyMark;

    /**
     * 强制性产品认证；字段名: COMPULSORY_CER；数据类型: VARCHAR2；长度: 8；可空: 是
     */
    @TableField("compulsory_cer")
    @Excel(name = "强制性产品认证", width = 15)
    @Schema(description = "强制性产品认证")
    private String compulsoryCer;

    /**
     * 有效标识；字段名: ENABLED；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("enabled")
    @Excel(name = "有效标识", width = 15)
    @Schema(description = "有效标识")
    private Integer enabled;

    /**
     * 删除标志；字段名: DELETEMARK；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("deletemark")
    @Excel(name = "删除标志", width = 15)
    @Schema(description = "删除标志")
    private Integer deletemark;

    /**
     * 创建日期；字段名: CREATEON；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("createon")
    @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createon;

    /**
     * CREATEUSER_ID；字段名: CREATEUSER_ID；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("createuser_id")
    @Excel(name = "CREATEUSER_ID", width = 15)
    @Schema(description = "CREATEUSER_ID")
    private String createuserId;

    /**
     * 创建用户；字段名: CREATEBY；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("createby")
    @Excel(name = "创建用户", width = 15)
    @Schema(description = "创建用户")
    private String createby;

    /**
     * 修改日期；字段名: MODIFIEDON；数据类型: TIMESTAMP；长度: 8；小数位: 6；可空: 是
     */
    @TableField("modifiedon")
    @Excel(name = "修改日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改日期")
    private Date modifiedon;

    /**
     * 修改用户主键；字段名: MODIFIEDUSERID；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("modifieduserid")
    @Excel(name = "修改用户主键", width = 15)
    @Schema(description = "修改用户主键")
    private String modifieduserid;

    /**
     * 修改用户；字段名: MODIFIEDBY；数据类型: VARCHAR2；长度: 256；可空: 是
     */
    @TableField("modifiedby")
    @Excel(name = "修改用户", width = 15)
    @Schema(description = "修改用户")
    private String modifiedby;

}
