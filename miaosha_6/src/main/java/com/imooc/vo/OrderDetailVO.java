package com.imooc.vo;

import com.imooc.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVO {

    private GoodsVO goodsVO;

    private OrderInfo orderInfo;

}
