package com.imooc.vo;

import com.imooc.domain.MiaoshaUser;
import lombok.Data;

@Data
public class GoodsDetailVO {

    private MiaoshaUser miaoshaUser;

    private GoodsVO goodsVO;

    private int miaoshaStatus = 0;  // 秒杀当前的状态

    private int remainSeconds = 0;  // 秒杀还有多少秒开始，用于倒计时
}
