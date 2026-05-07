package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.TrnHead;
import org.jeecg.modules.custom.cit.mapper.TrnHeadMapper;
import org.jeecg.modules.custom.cit.service.ITrnHeadService;
import org.springframework.stereotype.Service;

/**
 * 进口/出口转关单表头 TrnHead Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class TrnHeadServiceImpl extends ServiceImpl<TrnHeadMapper, TrnHead> implements ITrnHeadService {
}
