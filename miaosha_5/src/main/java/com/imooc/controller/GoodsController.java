package com.imooc.controller;

import com.imooc.domain.Goods;
import com.imooc.domain.MiaoshaUser;
import com.imooc.redis.GoodsKey;
import com.imooc.redis.RedisService;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.vo.GoodsDetailVO;
import com.imooc.vo.GoodsVO;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    /**
     * 页面缓存示例
     * 优化前：并发为1000（循环100次）时的QPS为1750
     * 优化后：并发为1000（循环100次）时的QPS为5200
     * @param model
     * @param miaoshaUser
     * @return
     */
    @RequestMapping(value = "/to_list", produces="text/html")
    @ResponseBody
    public String toList(HttpServletRequest request,
                         HttpServletResponse response,
                         Model model,
                         MiaoshaUser miaoshaUser) {

        // 设置用户信息
        model.addAttribute("user", miaoshaUser);

        // 取页面缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        // 查询商品列表
        List<GoodsVO> goods = goodsService.listGoodsVO();
        model.addAttribute("goodsList", goods);

        // 手动渲染
        SpringWebContext ctx = new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }

        return html;
    }

    /**
     * URL缓存示例
     * @param miaoshaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/to_detail0/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail0(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model,
                           MiaoshaUser miaoshaUser,
                           @PathVariable("goodsId") Long goodsId) {

        // 查缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

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

        // 手动渲染
        SpringWebContext ctx = new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);

        // 存缓存
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
        }

        return html;
    }

    /**
     * 页面静态化示例
     * @param miaoshaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/to_detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVO> toDetail(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model,
                           MiaoshaUser miaoshaUser,
                           @PathVariable("goodsId") Long goodsId) {

        // 查询商品详情
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);

        log.info("goodsVO: " + goodsVO);

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

        // 组织返回的VO对象
        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        goodsDetailVO.setMiaoshaUser(miaoshaUser);
        goodsDetailVO.setGoodsVO(goodsVO);
        goodsDetailVO.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVO.setRemainSeconds(remainSeconds);

        log.info("goodsDetailVO: " + goodsDetailVO);

        return Result.success(goodsDetailVO);
    }
}
