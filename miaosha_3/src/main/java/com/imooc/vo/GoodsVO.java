package com.imooc.vo;

import com.imooc.domain.Goods;
import lombok.Data;

import java.util.Date;

@Data
public class GoodsVO extends Goods {

    private Double miaoshaPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

}
