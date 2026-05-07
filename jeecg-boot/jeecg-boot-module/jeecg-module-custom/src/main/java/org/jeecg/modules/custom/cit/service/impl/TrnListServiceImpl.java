package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.TrnList;
import org.jeecg.modules.custom.cit.mapper.TrnListMapper;
import org.jeecg.modules.custom.cit.service.ITrnListService;
import org.springframework.stereotype.Service;

/**
 * 进口/出口转关单表体(提单) TrnList Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class TrnListServiceImpl extends ServiceImpl<TrnListMapper, TrnList> implements ITrnListService {
}
