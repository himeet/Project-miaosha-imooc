package com.imooc.controller;

import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.service.OrderService;
import com.imooc.vo.GoodsVO;
import com.imooc.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVO> detail(MiaoshaUser miaoshaUser, @RequestParam("orderId") Long orderId) {

        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = orderInfo.getGoodsId();
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderInfo(orderInfo);
        orderDetailVO.setGoodsVO(goodsVO);

        return Result.success(orderDetailVO);
    }

}
