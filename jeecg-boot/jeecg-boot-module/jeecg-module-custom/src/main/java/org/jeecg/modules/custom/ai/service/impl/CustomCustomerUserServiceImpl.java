package org.jeecg.modules.custom.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.ai.entity.CustomCustomerUser;
import org.jeecg.modules.custom.ai.mapper.CustomCustomerUserMapper;
import org.jeecg.modules.custom.ai.service.ICustomCustomerUserService;
import org.springframework.stereotype.Service;

@Service
public class CustomCustomerUserServiceImpl extends ServiceImpl<CustomCustomerUserMapper, CustomCustomerUser>
        implements ICustomCustomerUserService {
}
