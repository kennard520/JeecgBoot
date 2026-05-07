package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.DecRequestCert;
import org.jeecg.modules.custom.cit.mapper.DecRequestCertMapper;
import org.jeecg.modules.custom.cit.service.IDecRequestCertService;
import org.springframework.stereotype.Service;

/**
 * 申请单证信息表 DecRequestCert Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class DecRequestCertServiceImpl extends ServiceImpl<DecRequestCertMapper, DecRequestCert> implements IDecRequestCertService {
}
