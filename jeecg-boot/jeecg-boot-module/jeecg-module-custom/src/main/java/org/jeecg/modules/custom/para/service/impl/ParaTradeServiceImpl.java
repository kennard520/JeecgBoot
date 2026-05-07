package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaTrade;
import org.jeecg.modules.custom.para.mapper.ParaTradeMapper;
import org.jeecg.modules.custom.para.service.IParaTradeService;
import org.springframework.stereotype.Service;

/**
 * 贸易方式(监管方式) ParaTrade Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaTradeServiceImpl extends ServiceImpl<ParaTradeMapper, ParaTrade> implements IParaTradeService {
}
