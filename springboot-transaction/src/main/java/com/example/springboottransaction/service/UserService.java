package com.example.springboottransaction.service;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/27.
 */
public interface UserService {

    public int saveWithRollBack(String username) throws Exception;

    public int saveWithoutRollBack(String username) throws Exception;
}
