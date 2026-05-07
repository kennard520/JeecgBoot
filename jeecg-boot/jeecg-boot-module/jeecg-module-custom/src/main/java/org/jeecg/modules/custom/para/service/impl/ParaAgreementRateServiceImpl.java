package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaAgreementRate;
import org.jeecg.modules.custom.para.mapper.ParaAgreementRateMapper;
import org.jeecg.modules.custom.para.service.IParaAgreementRateService;
import org.springframework.stereotype.Service;

/**
 * PARA_AGREEMENT_RATE ParaAgreementRate Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaAgreementRateServiceImpl extends ServiceImpl<ParaAgreementRateMapper, ParaAgreementRate> implements IParaAgreementRateService {
}
