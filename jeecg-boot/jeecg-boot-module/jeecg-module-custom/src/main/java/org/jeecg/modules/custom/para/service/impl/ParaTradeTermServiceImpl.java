package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaTradeTerm;
import org.jeecg.modules.custom.para.mapper.ParaTradeTermMapper;
import org.jeecg.modules.custom.para.service.IParaTradeTermService;
import org.springframework.stereotype.Service;

/**
 * 贸易条款 ParaTradeTerm Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaTradeTermServiceImpl extends ServiceImpl<ParaTradeTermMapper, ParaTradeTerm> implements IParaTradeTermService {
}
