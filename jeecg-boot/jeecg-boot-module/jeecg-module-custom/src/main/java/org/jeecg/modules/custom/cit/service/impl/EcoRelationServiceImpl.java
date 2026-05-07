package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.EcoRelation;
import org.jeecg.modules.custom.cit.mapper.EcoRelationMapper;
import org.jeecg.modules.custom.cit.service.IEcoRelationService;
import org.springframework.stereotype.Service;

/**
 * 随附单证对应关系EcoRelation Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class EcoRelationServiceImpl extends ServiceImpl<EcoRelationMapper, EcoRelation> implements IEcoRelationService {
}
