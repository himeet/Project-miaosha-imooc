package com.imooc.service;

import com.imooc.controller.GoodsController;
import com.imooc.dao.OrderDAO;
import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.redis.OrderKey;
import com.imooc.redis.RedisService;
import com.imooc.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    OrderDAO orderDAO;

    @Autowired
    RedisService redisService;

//    /**
//     * 优化前：需要查数据库来获得订单信息
//     * @param userId
//     * @param goodsId
//     * @return
//     */
//    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
//        return orderDAO.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
//    }

    /**
     * 优化后：不去查数据库，查缓存获得订单信息
     * @param userId
     * @param goodsId
     * @return
     */
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVO goodsVO) {

        // 写入订单信息
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(miaoshaUser.getId());
        orderInfo.setGoodsId(goodsVO.getId());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goodsVO.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goodsVO.getMiaoshaPrice());
        orderInfo.setOrderChannel(0);
        orderInfo.setStatus(0);
        orderInfo.setCreateDate(new Date());

        Long orderId = orderDAO.insertOrder(orderInfo);  // TODO 这里返回的orderId一直是1，导致写入秒杀订单信息中的orderId是错误的！

        // 写入秒杀订单信息
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserId(miaoshaUser.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setGoodsId(goodsVO.getId());

        // 向数据库写入秒杀订单信息
        orderDAO.insertMiaoshaOrder(miaoshaOrder);

        // 向redis写入秒杀订单信息
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + miaoshaUser.getId() + "_" + goodsVO.getId(), miaoshaOrder);

        return orderInfo;
    }

    public OrderInfo getOrderById(Long orderId) {
        return orderDAO.getOrderById(orderId);
    }
}
