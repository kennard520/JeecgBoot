package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.DecList;
import org.jeecg.modules.custom.cit.mapper.DecListMapper;
import org.jeecg.modules.custom.cit.service.IDecListService;
import org.springframework.stereotype.Service;

/**
 * 进口/出口报关单表体 DecList Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class DecListServiceImpl extends ServiceImpl<DecListMapper, DecList> implements IDecListService {
}
