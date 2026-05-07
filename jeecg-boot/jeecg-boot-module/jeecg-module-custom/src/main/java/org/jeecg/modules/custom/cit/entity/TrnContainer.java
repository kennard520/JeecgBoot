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
import java.math.BigDecimal;

/**
 * 进口/出口提运单集装箱表 TrnContainer。
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
@Schema(description = "进口/出口提运单集装箱表 TrnContainer")
@TableName("TRN_CONTAINER")
public class TrnContainer implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，达梦自增标识列
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID，达梦自增标识列", width = 15)
    @Schema(description = "主键ID，达梦自增标识列")
    private Long id;
    /**
     * 转关单表头ID，父表关联ID
     */
    @TableField("trn_head_id")
    @Excel(name = "转关单表头ID，父表关联ID", width = 15)
    @Schema(description = "转关单表头ID，父表关联ID")
    private Long trnHeadId;
    /**
     * 转关单提单ID，父表关联ID
     */
    @TableField("trn_list_id")
    @Excel(name = "转关单提单ID，父表关联ID", width = 15)
    @Schema(description = "转关单提单ID，父表关联ID")
    private Long trnListId;
    /**
     * 集装箱号；字段代码: ContaNo；数据类型: String；长度: 11；暂存必填: 是；申报必填: 否
     */
    @TableField("conta_no")
    @Excel(name = "集装箱号", width = 15)
    @Schema(description = "集装箱号")
    private String contaNo;
    /**
     * 集装箱序号；字段代码: ContaSn；数据类型: Integer；长度: 3；暂存必填: 否；申报必填: 否
     */
    @TableField("conta_sn")
    @Excel(name = "集装箱序号", width = 15)
    @Schema(description = "集装箱序号")
    private Integer contaSn;
    /**
     * 集装箱规格；字段代码: ContaModel；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否
     */
    @TableField("conta_model")
    @Excel(name = "集装箱规格", width = 15)
    @Schema(description = "集装箱规格")
    private String contaModel;
    /**
     * 电子关锁号；字段代码: SealNo；数据类型: String；长度: 128；暂存必填: 否；申报必填: 否
     */
    @TableField("seal_no")
    @Excel(name = "电子关锁号", width = 15)
    @Schema(description = "电子关锁号")
    private String sealNo;
    /**
     * 境内运输工具名称；字段代码: TransName；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否
     */
    @TableField("trans_name")
    @Excel(name = "境内运输工具名称", width = 15)
    @Schema(description = "境内运输工具名称")
    private String transName;
    /**
     * 工具实际重量；字段代码: TransWeight；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否
     */
    @TableField("trans_weight")
    @Excel(name = "工具实际重量", width = 15)
    @Schema(description = "工具实际重量")
    private BigDecimal transWeight;
    /**
     * 关锁个数；字段代码: SealQty；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否
     */
    @TableField("seal_qty")
    @Excel(name = "关锁个数", width = 15)
    @Schema(description = "关锁个数")
    private String sealQty;
}
