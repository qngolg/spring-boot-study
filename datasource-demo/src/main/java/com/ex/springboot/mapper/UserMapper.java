package com.ex.springboot.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
public interface UserMapper {
    @Select("select name from user")
    List<String> getUsers(String dsName);

}
