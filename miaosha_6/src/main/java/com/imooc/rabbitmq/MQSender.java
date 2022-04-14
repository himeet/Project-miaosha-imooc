package com.imooc.rabbitmq;

import com.imooc.redis.RedisService;
import com.imooc.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

//    public void send(Object message) {
//        String msg = BeanUtil.beanToString(message);
//        log.info("MQ Send发送消息：" + message);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME, msg);
//    }
//
//    public void sendTopic(Object message) {
//        String msg = BeanUtil.beanToString(message);
//        log.info("MQ Send发送消息：" + message);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY1, msg + "-1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY2, msg + "-2");
//    }
//
//    public void sendFanout(Object message) {
//        String msg = BeanUtil.beanToString(message);
//        log.info("MQ Send发送消息：" + message);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "",msg);
//    }
//
//    public void sendHeaders(Object message) {
//        String msg = BeanUtil.beanToString(message);
//        log.info("MQ Send发送消息：" + message);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1", "value1");
//        properties.setHeader("header2", "value2");
//        Message obj = new Message(msg.getBytes(), properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
//    }

    public void sendMiaoshaMessage(MiaoshaMessage message) {
        String msg = BeanUtil.beanToString(message);
        log.info("MQ Send发送消息：" + message);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }
}
