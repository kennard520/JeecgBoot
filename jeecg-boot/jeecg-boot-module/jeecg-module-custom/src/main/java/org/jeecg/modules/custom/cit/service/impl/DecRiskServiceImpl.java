package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.DecRisk;
import org.jeecg.modules.custom.cit.mapper.DecRiskMapper;
import org.jeecg.modules.custom.cit.service.IDecRiskService;
import org.springframework.stereotype.Service;

/**
 * 风险评估信息DecRisk Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class DecRiskServiceImpl extends ServiceImpl<DecRiskMapper, DecRisk> implements IDecRiskService {
}
