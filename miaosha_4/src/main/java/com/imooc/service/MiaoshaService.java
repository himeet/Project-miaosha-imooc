package com.imooc.service;

import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;  // 这里不要隐引入GoodsDAO，提倡Service只引入自己对应的DAO，所以这里引入Goods的Service

    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVO goodsVO) {
        // 减库存
        goodsService.reduceStock(goodsVO);

        // 下订单，写入秒杀订单（order_info miaosha_order）
        return orderService.createOrder(miaoshaUser, goodsVO);

        //





    }
}
