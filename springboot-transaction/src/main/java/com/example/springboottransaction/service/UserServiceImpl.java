package com.example.springboottransaction.service;

import com.example.springboottransaction.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/27.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveWithRollBack(String username) throws Exception {
        int flag = userMapper.insert(username);
        if(username.equals("AAA")){
            throw new Exception("AAA已经存在了，事务回滚");
        }
        return flag;
    }

    @Override
    public int saveWithoutRollBack(String username) throws Exception {
        int flag = userMapper.insert(username);
        if(username.equals("AAA")){
            throw new Exception("AAA已经存在了，事务不回滚");
        }
        return flag;
    }
}
