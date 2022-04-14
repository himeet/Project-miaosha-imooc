package com.imooc.service;

import com.imooc.dao.GoodsDAO;
import com.imooc.domain.Goods;
import com.imooc.domain.MiaoshaGoods;
import com.imooc.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDAO goodsDAO;

    public List<GoodsVO> listGoodsVO() {
        return goodsDAO.listGoodsVO();
    }

    public GoodsVO getGoodsVOByGoodsId(Long goodsId) {
        return goodsDAO.getGoodsVOByGoodsId(goodsId);
    }

    public void reduceStock(GoodsVO goodsVO) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goodsVO.getId());
        miaoshaGoods.setStockCount(goodsVO.getGoodsStock() - 1);
        goodsDAO.reduceStock(miaoshaGoods);
    }
}
