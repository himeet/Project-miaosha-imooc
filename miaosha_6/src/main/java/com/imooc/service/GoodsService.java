package com.imooc.service;

import com.imooc.dao.GoodsDAO;
import com.imooc.domain.MiaoshaGoods;
import com.imooc.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDAO goodsDAO;

    private static Logger log = LoggerFactory.getLogger(GoodsService.class);

    public List<GoodsVO> listGoodsVO() {
        return goodsDAO.listGoodsVO();
    }

    public GoodsVO getGoodsVOByGoodsId(Long goodsId) {
        return goodsDAO.getGoodsVOByGoodsId(goodsId);
    }

    public Boolean reduceStock(GoodsVO goodsVO) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goodsVO.getId());
        int ret = goodsDAO.reduceStock(miaoshaGoods);
        return ret > 0;
    }

    public void resetStock(List<GoodsVO> goodsList) {
        for(GoodsVO goods : goodsList ) {
            MiaoshaGoods g = new MiaoshaGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDAO.resetStock(g);
        }
    }

}
