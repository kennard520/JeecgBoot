package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.DecTpAccess;
import org.jeecg.modules.custom.cit.mapper.DecTpAccessMapper;
import org.jeecg.modules.custom.cit.service.IDecTpAccessService;
import org.springframework.stereotype.Service;

/**
 * 两段准入申请DecTpAccess Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class DecTpAccessServiceImpl extends ServiceImpl<DecTpAccessMapper, DecTpAccess> implements IDecTpAccessService {
}
