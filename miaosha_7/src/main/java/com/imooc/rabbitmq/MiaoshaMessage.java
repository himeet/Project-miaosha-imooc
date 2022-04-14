package com.imooc.rabbitmq;

import com.imooc.domain.MiaoshaUser;
import lombok.Data;

@Data
public class MiaoshaMessage {

    private MiaoshaUser miaoshaUser;

    private Long goodsId;

}
