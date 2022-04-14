package com.imooc.dao;

import com.imooc.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MiaoshaUserDAO {

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getById(@Param("id") long id);

}
