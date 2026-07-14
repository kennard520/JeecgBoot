package org.jeecg.modules.custom.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.ai.entity.CustomUserAgent;
import org.jeecg.modules.custom.ai.mapper.CustomUserAgentMapper;
import org.jeecg.modules.custom.ai.service.ICustomUserAgentService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserAgentServiceImpl extends ServiceImpl<CustomUserAgentMapper, CustomUserAgent>
        implements ICustomUserAgentService {
}
