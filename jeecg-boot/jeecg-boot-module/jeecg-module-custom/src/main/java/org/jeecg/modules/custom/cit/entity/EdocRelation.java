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
 * 电子随附单据关联关系信息EdocRealation。
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
@Schema(description = "电子随附单据关联关系信息EdocRealation")
@TableName("EDOC_RELATION")
public class EdocRelation implements Serializable {
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
     * 文件名、随附单据编号（文件名命名规则是：申报口岸+随附单据类别代码+IM+18位流水号+.pdf）；字段代码: EdocID；数据类型: String；长度: 64；暂存必填: 是/是；申报必填: 否
     */
    @TableField("edoc_id")
    @Excel(name = "文件名、随附单据编号（文件名命名规则是：申报口岸+随附单据类别代码+IM+18位流水号+.pdf）", width = 15)
    @Schema(description = "文件名、随附单据编号（文件名命名规则是：申报口岸+随附单据类别代码+IM+18位流水号+.pdf）")
    private String edocId;
    /**
     * 随附单证类别；字段代码: EdocCode；数据类型: String；长度: 8；暂存必填: 是/是；申报必填: 否；说明: 如： 00000001:发票 00000002:装箱单 00000003:提/运单 00000004:合同 10000001：代理报关委托协议（电子） 10000002：减免税货物税款担保证明 10000003：减免税货物税款担保延期证明 具体详见海关总署网站《随附单据代码参数表》
     */
    @TableField("edoc_code")
    @Excel(name = "随附单证类别", width = 15)
    @Schema(description = "随附单证类别")
    private String edocCode;
    /**
     * 随附单据格式类型；字段代码: EdocFomatType；数据类型: String；长度: 2；暂存必填: 是/是；申报必填: 否；说明: S:结构化 US:非结构化（pdf文件填写US）
     */
    @TableField("edoc_fomat_type")
    @Excel(name = "随附单据格式类型", width = 15)
    @Schema(description = "随附单据格式类型")
    private String edocFomatType;
    /**
     * 操作说明（重传原因）；字段代码: OpNote；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否
     */
    @TableField("op_note")
    @Excel(name = "操作说明（重传原因）", width = 15)
    @Schema(description = "操作说明（重传原因）")
    private String opNote;
    /**
     * 随附单据文件企业名；字段代码: EdocCopId；数据类型: String；长度: 64；暂存必填: 否；申报必填: 否
     */
    @TableField("edoc_cop_id")
    @Excel(name = "随附单据文件企业名", width = 15)
    @Schema(description = "随附单据文件企业名")
    private String edocCopId;
    /**
     * 所属单位海关编号；字段代码: EdocOwnerCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否
     */
    @TableField("edoc_owner_code")
    @Excel(name = "所属单位海关编号", width = 15)
    @Schema(description = "所属单位海关编号")
    private String edocOwnerCode;
    /**
     * 签名单位代码；字段代码: SignUnit；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否
     */
    @TableField("sign_unit")
    @Excel(name = "签名单位代码", width = 15)
    @Schema(description = "签名单位代码")
    private String signUnit;
    /**
     * 签名时间；字段代码: SignTime；数据类型: String；长度: 17；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd hh:mm:ss
     */
    @TableField("sign_time")
    @Excel(name = "签名时间", width = 15)
    @Schema(description = "签名时间")
    private String signTime;
    /**
     * 所属单位名称；字段代码: EdocOwnerName；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否
     */
    @TableField("edoc_owner_name")
    @Excel(name = "所属单位名称", width = 15)
    @Schema(description = "所属单位名称")
    private String edocOwnerName;
    /**
     * 随附单据文件大小；字段代码: EdocSize；数据类型: String；长度: 12；暂存必填: 否；申报必填: 否
     */
    @TableField("edoc_size")
    @Excel(name = "随附单据文件大小", width = 15)
    @Schema(description = "随附单据文件大小")
    private String edocSize;
    /**
     * 商品项号关系；字段代码: GNoStr；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否；说明: 商品项号关系，多个商品项号用英文半角逗号分隔，如“1,3”。
     */
    @TableField("g_no_str")
    @Excel(name = "商品项号关系", width = 15)
    @Schema(description = "商品项号关系")
    private String gNoStr;
}
