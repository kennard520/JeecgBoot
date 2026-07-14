package org.jeecg.modules.custom.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.ai.entity.CustomAiAgent;
import org.jeecg.modules.custom.ai.mapper.CustomAiAgentMapper;
import org.jeecg.modules.custom.ai.service.ICustomAiAgentService;
import org.springframework.stereotype.Service;

@Service
public class CustomAiAgentServiceImpl extends ServiceImpl<CustomAiAgentMapper, CustomAiAgent>
        implements ICustomAiAgentService {
}
