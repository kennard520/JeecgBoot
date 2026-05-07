package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaPortLin;
import org.jeecg.modules.custom.para.mapper.ParaPortLinMapper;
import org.jeecg.modules.custom.para.service.IParaPortLinService;
import org.springframework.stereotype.Service;

/**
 * 港口 ParaPortLin Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaPortLinServiceImpl extends ServiceImpl<ParaPortLinMapper, ParaPortLin> implements IParaPortLinService {
}
