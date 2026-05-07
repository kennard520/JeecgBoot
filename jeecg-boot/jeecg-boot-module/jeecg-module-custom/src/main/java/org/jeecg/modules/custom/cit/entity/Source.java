package org.jeecg.modules.custom.cit.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author wayne
 * @version 1.0.0
 * @since 2026/5/6 14:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "报税单SddTax")
@TableName("SDD_TAX")
public class Source {

}
