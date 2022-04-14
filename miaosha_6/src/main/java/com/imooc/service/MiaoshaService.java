package com.imooc.service;

import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.redis.GoodsKey;
import com.imooc.redis.MiaoshaKey;
import com.imooc.redis.RedisService;
import com.imooc.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;  // 这里不要隐引入GoodsDAO，提倡Service只引入自己对应的DAO，所以这里引入Goods的Service

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVO goodsVO) {

        // 减库存
        Boolean success = goodsService.reduceStock(goodsVO);
        if (success) {
            // 下订单，写入秒杀订单（order_info miaosha_order）
            return orderService.createOrder(miaoshaUser, goodsVO);
        } else {
            setGoodsOver(goodsVO.getId());
            return null;
        }
    }

    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    public Long getMiaoshaResult(Long userId, Long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (miaoshaOrder != null) {  // 秒杀成功
            return miaoshaOrder.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1L;
            } else {
                return 0L;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }

    public void reset(List<GoodsVO> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }
}
