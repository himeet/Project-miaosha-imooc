package com.imooc.dao;

import com.imooc.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDAO {

    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") int id);

    @Insert("insert into user (id, name) values (#{id}, #{name})")
    public int insert(User user);

}
