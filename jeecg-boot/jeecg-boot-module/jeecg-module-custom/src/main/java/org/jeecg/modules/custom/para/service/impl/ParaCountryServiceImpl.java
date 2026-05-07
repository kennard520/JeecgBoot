package org.jeecg.modules.custom.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.para.entity.ParaCountry;
import org.jeecg.modules.custom.para.mapper.ParaCountryMapper;
import org.jeecg.modules.custom.para.service.IParaCountryService;
import org.springframework.stereotype.Service;

/**
 * 国别(地区) ParaCountry Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class ParaCountryServiceImpl extends ServiceImpl<ParaCountryMapper, ParaCountry> implements IParaCountryService {
}
