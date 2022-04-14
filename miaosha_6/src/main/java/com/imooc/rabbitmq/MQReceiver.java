package com.imooc.rabbitmq;

import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.exception.GlobalException;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.service.MiaoshaService;
import com.imooc.service.OrderService;
import com.imooc.utils.BeanUtil;
import com.imooc.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

//    /**
//     * Direct模式
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.QUEUE_NAME)
//    public void receive(String message) {
//        log.info("[Direct]MQ Receiver接收到消息：" + message);
//    }
//
//    /**
//     * Topic模式
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_NAME1)
//    public void receiveTopic1(String message) {
//        log.info("[Topic]MQ Receiver queue1 接收到消息：" + message);
//    }
//
//    /**
//     * Topic模式
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_NAME2)
//    public void receiveTopic2(String message) {
//        log.info("[Topic]MQ Receiver queue2 接收到消息：" + message);
//    }
//
//    /**
//     * Fanout模式
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.FANOUT_QUEUE_NAME1)
//    public void receiveFanout1(String message) {
//        log.info("[Fanout]MQ Receiver queue1 接收到消息：" + message);
//    }
//
//    /**
//     * Fanout模式
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.FANOUT_QUEUE_NAME2)
//    public void receiveFanout2(String message) {
//        log.info("[Fanout]MQ Receiver queue2 接收到消息：" + message);
//    }
//
//    /**
//     * Headers模式
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE_NAME)
//    public void receiveHeaders(byte[] message) {
//        log.info("[Headers]MQ Receiver queue接收到消息：" + new String(message));
//    }

    /**
     * Headers模式
     * @param message
     */
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receiveMiaosha(String message) {
        log.info("MQ Receiver queue接收到消息：" + message);
        MiaoshaMessage miaoshaMessage = BeanUtil.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser miaoshaUser = miaoshaMessage.getMiaoshaUser();
        Long goodsId = miaoshaMessage.getGoodsId();

        // 判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        Integer stockCount = goodsVO.getStockCount();
        if (stockCount <= 0) {
            return;
        }

        // 判断该用户是否已经秒杀到了
        MiaoshaOrder miaoshaOrder =
                orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if (miaoshaOrder != null) {
            return;
        }

        // 减库存 下订单 写入秒杀订单（一个事务内执行）
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser, goodsVO);
    }
}
