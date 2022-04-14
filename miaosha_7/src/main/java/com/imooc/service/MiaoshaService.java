package com.imooc.service;

import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.MiaoshaUser;
import com.imooc.domain.OrderInfo;
import com.imooc.redis.MiaoshaKey;
import com.imooc.redis.RedisService;
import com.imooc.utils.MD5Util;
import com.imooc.utils.UUIDUtil;
import com.imooc.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;  // 这里不要隐引入GoodsDAO，提倡Service只引入自己对应的DAO，所以这里引入Goods的Service

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVO goodsVO) {

        // 减库存
        Boolean success = goodsService.reduceStock(goodsVO);
        if (success) {
            // 下订单，写入秒杀订单（order_info miaosha_order）
            return orderService.createOrder(miaoshaUser, goodsVO);
        } else {
            setGoodsOver(goodsVO.getId());
            return null;
        }
    }

    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    public Long getMiaoshaResult(Long userId, Long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (miaoshaOrder != null) {  // 秒杀成功
            return miaoshaOrder.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1L;
            } else {
                return 0L;
            }
        }
    }


    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }

    public void reset(List<GoodsVO> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    public Boolean checkPath(MiaoshaUser miaoshaUser, Long goodsId, String path) {
        if (miaoshaUser == null || goodsId <= 0 || path == null) {
            return false;
        }
        String pathOld =
                redisService.get(MiaoshaKey.getMiaoshaPath, "" + miaoshaUser.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }


    public String createMiaoshaPath(MiaoshaUser miaoshaUser, Long goodsId) {
        if (miaoshaUser == null || goodsId <= 0) {
            return null;
        }
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + miaoshaUser.getId() + "_" + goodsId, path);
        return path;
    }

    public BufferedImage createVerifyCode(MiaoshaUser miaoshaUser, Long goodsId) {
        if (miaoshaUser == null || goodsId <= 0) {
            return null;
        }

        int width = 80;
        int height = 32;

        // 创建图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 设置背景颜色
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);

        // 绘制border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);

        // 创建一个随机实例去生成验证码
        Random random = new Random();

        // 做一些混合
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.drawOval(x, y, 0, 0);  // 绘制50个干扰点
        }

        // 生成一个随机验证码
        String verifyCode = generateVerifyCode(random);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 18));
        g.drawString(verifyCode, 8, 24);
        g.dispose();

        // 把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId() + "," + goodsId, rnd);

        // 输出图片
        return image;
    }

    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * 加 减 乘
     * @param random
     * @return
     */
    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);

        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];

        String exp = "" + num1 + op1 + num2 + op2 + num3;

        return exp;
    }

    public Boolean checkVerifyCode(MiaoshaUser miaoshaUser, Long goodsId, Integer verifyCode) {

        if (miaoshaUser == null || goodsId <= 0) {
            return false;
        }

        Integer verifyCodeOld =
                redisService.get(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId() + "," + goodsId, Integer.class);
        if (verifyCodeOld == null || verifyCodeOld - verifyCode != 0) {
            return false;
        }

        redisService.del(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId() + "," + goodsId);

        return true;
    }
}
