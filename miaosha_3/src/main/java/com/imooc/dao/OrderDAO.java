package com.imooc.dao;

import com.imooc.domain.MiaoshaOrder;
import com.imooc.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDAO {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long userId,
                                                       @Param("goodsId") Long goodsid);
    @Insert("insert into " +
            "order_info" +
            "(user_id, goods_id, delivery_addr_id, goods_name, goods_count, goods_price, order_channel, status, create_date)" +
            " values " +
            "(#{userId}, #{goodsId}, #{deliveryAddrId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn="id", keyProperty = "id", resultType = Long.class, before = false, statement = "select last_insert_id()")
    public Long insertOrder(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, order_id, goods_id) values (#{userId}, #{orderId}, #{goodsId})")
    public void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
}
