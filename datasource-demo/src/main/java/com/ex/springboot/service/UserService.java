package com.ex.springboot.service;

import java.util.List;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
public interface UserService {

    List<String> getUsers(String dsName);
}
