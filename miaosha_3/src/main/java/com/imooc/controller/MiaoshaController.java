package com.imooc.controller;

import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.result.CodeMsg;
import com.imooc.service.GoodsService;
import com.imooc.service.MiaoshaService;
import com.imooc.service.OrderService;
import com.imooc.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String doMiaosha(Model model,
                            MiaoshaUser miaoshaUser,
                            @RequestParam("goodsId") Long goodsId) {
        model.addAttribute("user", miaoshaUser);
        if (miaoshaUser == null) {
            return "login";
        }

        // 判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        Integer stockCount = goodsVO.getStockCount();
        if (stockCount <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        // 判断该用户是否已经秒杀到了
        MiaoshaOrder miaoshaOrder =
                orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if (miaoshaOrder != null) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        // 减库存 下订单 写入秒杀订单（一个事务内执行）
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser, goodsVO);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVO);

        return "order_detail";
     }

}
