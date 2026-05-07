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
 * 贸易方式(监管方式) ParaTrade。
 *
 * <p>根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "贸易方式(监管方式)")
@TableName("PARA_TRADE")
public class ParaTrade implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键；字段名: ID；数据类型: VARCHAR2；长度: 64；可空: 否
     */
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "主键", width = 15)
    @Schema(description = "主键")
    private String id;

    /**
     * 编码；字段名: TRADE_MODE；数据类型: VARCHAR2；长度: 8；可空: 是
     */
    @TableField("trade_mode")
    @Excel(name = "编码", width = 15)
    @Schema(description = "编码")
    private String tradeMode;

    /**
     * 简称；字段名: ABBR_TRADE；数据类型: VARCHAR2；长度: 32；可空: 是
     */
    @TableField("abbr_trade")
    @Excel(name = "简称", width = 15)
    @Schema(description = "简称")
    private String abbrTrade;

    /**
     * 全称；字段名: FULL_TRADE；数据类型: VARCHAR2；长度: 128；可空: 是
     */
    @TableField("full_trade")
    @Excel(name = "全称", width = 15)
    @Schema(description = "全称")
    private String fullTrade;

    /**
     * 分类:0表示保税;1表示一般贸易;2表示退运;3表示设备;4表示内销补税;5表示余料结转;6表示深加工;7表示暂时进出口；字段名: LIST_TYPE；数据类型: VARCHAR2；长度: 1；可空: 是
     */
    @TableField("list_type")
    @Excel(name = "分类:0表示保税;1表示一般贸易;2表示退运;3表示设备;4表示内销补税;5表示余料结转;6表示深加工;7表示暂时进出口", width = 15)
    @Schema(description = "分类:0表示保税;1表示一般贸易;2表示退运;3表示设备;4表示内销补税;5表示余料结转;6表示深加工;7表示暂时进出口")
    private String listType;

    /**
     * 进出口标志:I进口;E出口;A都有；字段名: I_E_MARK；数据类型: VARCHAR2；长度: 1；可空: 是
     */
    @TableField("i_e_mark")
    @Excel(name = "进出口标志:I进口;E出口;A都有", width = 15)
    @Schema(description = "进出口标志:I进口;E出口;A都有")
    private String iEMark;

    /**
     * 保税标志:Y保税;N非保税;A都有；字段名: BOND_MARK；数据类型: VARCHAR2；长度: 1；可空: 是
     */
    @TableField("bond_mark")
    @Excel(name = "保税标志:Y保税;N非保税;A都有", width = 15)
    @Schema(description = "保税标志:Y保税;N非保税;A都有")
    private String bondMark;

    /**
     * 排序码；字段名: SEQ_NO；数据类型: INT；长度: 4；可空: 是
     */
    @TableField("seq_no")
    @Excel(name = "排序码", width = 15)
    @Schema(description = "排序码")
    private Integer seqNo;

}
