package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaLevymode;
import org.jeecg.modules.custom.para.mapper.ParaLevymodeMapper;
import org.jeecg.modules.custom.para.service.IParaLevymodeService;
import org.springframework.stereotype.Service;

/**
 * 征免方式 ParaLevymode Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaLevymodeServiceImpl extends ServiceImpl<ParaLevymodeMapper, ParaLevymode> implements IParaLevymodeService {
}
