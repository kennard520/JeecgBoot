package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaDest;
import org.jeecg.modules.custom.para.mapper.ParaDestMapper;
import org.jeecg.modules.custom.para.service.IParaDestService;
import org.springframework.stereotype.Service;

/**
 * 行政区域 ParaDest Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaDestServiceImpl extends ServiceImpl<ParaDestMapper, ParaDest> implements IParaDestService {
}
