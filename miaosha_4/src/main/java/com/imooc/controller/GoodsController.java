package com.imooc.controller;

import com.imooc.domain.MiaoshaUser;
import com.imooc.service.GoodsService;
import com.imooc.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    /**
     * 并发为1000（循环100次）时的QPS为1750
     * @param model
     * @param miaoshaUser
     * @return
     */
    @RequestMapping("/to_list")
    public String toList(Model model, MiaoshaUser miaoshaUser) {
        model.addAttribute("user", miaoshaUser);

        // 查询商品列表
        List<GoodsVO> goods = goodsService.listGoodsVO();
        model.addAttribute("goodsList", goods);

        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser miaoshaUser,
                           @PathVariable("goodsId") Long goodsId) {
        model.addAttribute("user", miaoshaUser);

        // 查询商品详情
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        model.addAttribute("goods", goodsVO);

        // 获取此时刻该商品的秒杀状态
        long startTime = goodsVO.getStartDate().getTime();
        long endTime = goodsVO.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();

        int miaoshaStatus = 0;  // 秒杀当前的状态
        int remainSeconds = 0;  // 秒杀还有多少秒开始，用于倒计时
        if (nowTime < startTime) {  // 秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startTime - nowTime) / 1000);
        } else if (nowTime > endTime) {  // 秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {  // 秒杀正在进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goods_detail";
    }
}
