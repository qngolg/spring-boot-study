package com.ex.springboot.service.impl;

import com.ex.springboot.mapper.UserMapper;
import com.ex.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<String> getUsers(String dsname) {
        return userMapper.getUsers(dsname);
    }
}
