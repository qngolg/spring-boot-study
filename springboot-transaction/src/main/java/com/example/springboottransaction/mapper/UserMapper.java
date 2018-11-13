package com.example.springboottransaction.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/27.
 */
@Mapper
public interface UserMapper {
    int insert(String username);
}
