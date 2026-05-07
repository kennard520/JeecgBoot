package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.CitAttributes;
import org.jeecg.modules.custom.cit.mapper.CitAttributesMapper;
import org.jeecg.modules.custom.cit.service.ICitAttributesService;
import org.springframework.stereotype.Service;

/**
 * 报文属性attributes Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class CitAttributesServiceImpl extends ServiceImpl<CitAttributesMapper, CitAttributes> implements ICitAttributesService {
}
