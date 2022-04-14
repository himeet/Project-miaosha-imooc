package com.imooc.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {
    /** Direct模式 */
    public static final String QUEUE_NAME = "queue";

    /** Topic模式 */
    public static final String TOPIC_QUEUE_NAME1 = "topic.queue1";
    public static final String TOPIC_QUEUE_NAME2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE = "topic.exchange";
    public static final String ROUTING_KEY1 = "topic.key1";
    public static final String ROUTING_KEY2 = "topic.#";  // #为通配符，代表多个

    /** Fanout模式 */
    public static final String FANOUT_QUEUE_NAME1 = "fanout.queue";
    public static final String FANOUT_QUEUE_NAME2 = "fanout.queue";
    public static final String FANOUT_EXCHANGE = "fanout.exchange";

    /** Headers模式 */
    public static final String HEADERS_QUEUE_NAME = "headers.queue";
    public static final String HEADERS_EXCHANGE = "headers.exchange";

    /** 秒杀接口相关 */
    public static final String MIAOSHA_QUEUE = "miaosha.queue";

    /**
     * 秒杀接口的queue
     * 创建一个queue
     * @return
     */
    @Bean
    public Queue miaoshaQueue() {
        return new Queue(MIAOSHA_QUEUE, true);  // 队列名称为queue，是否开始持久化：true
    }

    /**
     * Direct模式
     * 创建一个queue
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);  // 队列名称为queue，是否开始持久化：true
    }

    /**
     * Topic模式
     * 创建第一个queue
     * @return
     */
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE_NAME1, true);
    }

    /**
     * Topic模式
     * 创建第二个queue
     * @return
     */
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE_NAME2, true);
    }

    /**
     * 创建一个交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    /**
     * 将queue1与交换机绑定
     * @return
     */
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);  // topic.key1
    }

    /**
     * 将queue2与交换机绑定
     * @return
     */
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);  // topic.#
    }

    /**
     * Fanout模式（广播模式）
     * @return
     */
    @Bean
    public Queue fanoutQueue1() {
        return new Queue(FANOUT_QUEUE_NAME1, true);
    }

    /**
     * Fanout模式（广播模式）
     * @return
     */
    @Bean
    public Queue fanoutQueue2() {
        return new Queue(FANOUT_QUEUE_NAME2, true);
    }

    /**
     * 创建fanout的交换机
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    /**
     * 将fanout的交换机与fanout的queue绑定
     * @return
     */
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    /**
     * 将fanout的交换机与fanout的queue绑定
     * @return
     */
    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }

    /**
     * Headers模式
     * 创建一个queue
     * @return
     */
    @Bean
    public Queue headersQueue() {
        return new Queue(HEADERS_QUEUE_NAME, true);  // 队列名称为queue，是否开始持久化：true
    }

    /**
     * 创建headers的交换机
     * @return
     */
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    /**
     * 将fanout的交换机与fanout的queue绑定
     * @return
     */
    @Bean
    public Binding headersBinding() {
        Map<String, Object> map = new HashMap<>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
    }
}
