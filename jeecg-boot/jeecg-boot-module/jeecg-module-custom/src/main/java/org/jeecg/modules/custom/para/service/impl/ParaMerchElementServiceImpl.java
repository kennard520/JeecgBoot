package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaMerchElement;
import org.jeecg.modules.custom.para.mapper.ParaMerchElementMapper;
import org.jeecg.modules.custom.para.service.IParaMerchElementService;
import org.springframework.stereotype.Service;

/**
 * 申报要素拉平格式 ParaMerchElement Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaMerchElementServiceImpl extends ServiceImpl<ParaMerchElementMapper, ParaMerchElement> implements IParaMerchElementService {
}
