package com.imooc.controller;

import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.rabbitmq.MQSender;
import com.imooc.rabbitmq.MiaoshaMessage;
import com.imooc.redis.GoodsKey;
import com.imooc.redis.MiaoshaKey;
import com.imooc.redis.OrderKey;
import com.imooc.redis.RedisService;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.service.MiaoshaService;
import com.imooc.service.OrderService;
import com.imooc.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 在系统初始化时，将商品库存加载到Redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVO> goodsVOList = goodsService.listGoodsVO();
        if (goodsVOList == null) {
            return;
        }
        for (GoodsVO goodsVO : goodsVOList) {
            redisService.set(GoodsKey.getMiaoshaGooodsStock, "" + goodsVO.getId(), goodsVO.getStockCount());
            localOverMap.put(goodsVO.getId(), false);
        }
    }

    /**
     * 秒杀静态化
     * 并发数为1000（循环100次）时QPS为
     * <p>
     * TODO:这里的多线程访问是有问题的，减库存的数量与订单数对应不起来
     * <p>
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
    public Result<Integer> doMiaosha(Model model,
                                     MiaoshaUser miaoshaUser,
                                     @RequestParam("goodsId") Long goodsId) {

//        log.info("当前线程名字：" + Thread.currentThread().getName());  // TODO del

        // 判断用户是否登录
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 利用内存标记，减少redis访问
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGooodsStock, "" + goodsId);  // 返回的是减少后的
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 入队
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setMiaoshaUser(miaoshaUser);
        miaoshaMessage.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(miaoshaMessage);

        return Result.success(0);  // 客户端显示排队中
    }

    /**
     * 获取秒杀结果
     * 返回orderId表示秒杀成功，返回-1表示秒杀失败，返回0表示排队中
     *
     * @param model
     * @param miaoshaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result")
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,
                                      MiaoshaUser miaoshaUser,
                                      @RequestParam("goodsId") Long goodsId) {
        // 判断用户是否登录
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        Long result = miaoshaService.getMiaoshaResult(miaoshaUser.getId(), goodsId);

        return Result.success(result);
    }

    /**
     * 为了压测，重置数据库和redis环境的数据
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> reset(Model model) {
        List<GoodsVO> goodsVOList = goodsService.listGoodsVO();
        for (GoodsVO goodsVO : goodsVOList) {
            goodsVO.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGooodsStock, "" + goodsVO.getId(), 10);
            localOverMap.put(goodsVO.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsVOList);
        return Result.success("环境数据重置成功！");
    }

}
