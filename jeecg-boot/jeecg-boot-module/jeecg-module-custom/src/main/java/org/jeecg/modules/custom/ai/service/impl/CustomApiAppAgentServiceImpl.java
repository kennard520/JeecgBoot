package org.jeecg.modules.custom.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.ai.entity.CustomApiAppAgent;
import org.jeecg.modules.custom.ai.mapper.CustomApiAppAgentMapper;
import org.jeecg.modules.custom.ai.service.ICustomApiAppAgentService;
import org.springframework.stereotype.Service;

@Service
public class CustomApiAppAgentServiceImpl extends ServiceImpl<CustomApiAppAgentMapper, CustomApiAppAgent>
        implements ICustomApiAppAgentService {
}
