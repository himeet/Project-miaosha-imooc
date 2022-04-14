package com.imooc.service;

import com.imooc.dao.OrderDAO;
import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDAO orderDAO;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
        return orderDAO.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
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

        Long orderId = orderDAO.insertOrder(orderInfo);

        // 写入秒杀订单信息
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserId(miaoshaUser.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setGoodsId(goodsVO.getId());

        orderDAO.insertMiaoshaOrder(miaoshaOrder);

        return orderInfo;
    }
}
