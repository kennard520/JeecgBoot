package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaHscodeCmp;
import org.jeecg.modules.custom.para.mapper.ParaHscodeCmpMapper;
import org.jeecg.modules.custom.para.service.IParaHscodeCmpService;
import org.springframework.stereotype.Service;

/**
 * 商品编码变化 ParaHscodeCmp Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaHscodeCmpServiceImpl extends ServiceImpl<ParaHscodeCmpMapper, ParaHscodeCmp> implements IParaHscodeCmpService {
}
