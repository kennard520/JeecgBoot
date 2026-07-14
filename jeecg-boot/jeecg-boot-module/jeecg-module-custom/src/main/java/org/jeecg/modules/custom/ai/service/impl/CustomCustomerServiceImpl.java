package org.jeecg.modules.custom.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.ai.entity.CustomCustomer;
import org.jeecg.modules.custom.ai.mapper.CustomCustomerMapper;
import org.jeecg.modules.custom.ai.service.ICustomCustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomCustomerServiceImpl extends ServiceImpl<CustomCustomerMapper, CustomCustomer>
        implements ICustomCustomerService {
}
