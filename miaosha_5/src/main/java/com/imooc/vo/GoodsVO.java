package com.imooc.vo;

import com.imooc.domain.Goods;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GoodsVO extends Goods {

    private Double miaoshaPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

    @Override
    public String toString() {
        return "GoodsVO{" +
                "miaoshaPrice=" + miaoshaPrice +
                ", stockCount=" + stockCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                "} " + super.toString();
    }
}
