package com.imooc.controller;

import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.service.MiaoshaService;
import com.imooc.service.OrderService;
import com.imooc.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    /**
     * 秒杀静态化
     * 并发数为1000（循环100次）时QPS为
     *
     * TODO:这里的多线程访问是有问题的，减库存的数量与订单数对应不起来
     * 
     * GET POST有什么区别？
     * GET是幂等的，代表从服务端获取数据，不管GET执行多少次都不会对服务端的数据产生影响
     * POST不是幂等的，代表向服务端发送数据，遇到需要修改服务端数据的情况，则应该使用POST
     *
     * @param model
     * @param miaoshaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> doMiaosha(Model model,
                                       MiaoshaUser miaoshaUser,
                                       @RequestParam("goodsId") Long goodsId) {

//        log.info("当前线程名字：" + Thread.currentThread().getName());  // TODO del

        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        Integer stockCount = goodsVO.getStockCount();
        if (stockCount <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 判断该用户是否已经秒杀到了
        MiaoshaOrder miaoshaOrder =
                orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEATE_MIAO_SHA);
        }

        // 减库存 下订单 写入秒杀订单（一个事务内执行）
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser, goodsVO);

        return Result.success(orderInfo);
     }

}
