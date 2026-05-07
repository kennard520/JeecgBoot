package org.jeecg.modules.custom.cit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.custom.cit.entity.TrnContaGoodsList;
import org.jeecg.modules.custom.cit.mapper.TrnContaGoodsListMapper;
import org.jeecg.modules.custom.cit.service.ITrnContaGoodsListService;
import org.springframework.stereotype.Service;

/**
 * 进口/出口提运单集装箱商品装配表 TrnContaGoodsList Service 实现。
 *
 * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>
 *
 * @author jeecg-boot
 * @since 3.9.1
 */
@Service
public class TrnContaGoodsListServiceImpl extends ServiceImpl<TrnContaGoodsListMapper, TrnContaGoodsList> implements ITrnContaGoodsListService {
}
