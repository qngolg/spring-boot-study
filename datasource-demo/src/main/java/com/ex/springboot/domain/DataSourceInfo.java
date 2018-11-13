package com.ex.springboot.domain;

import lombok.Data;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
@Data
public class DataSourceInfo {
    private Long id;
    private String type;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String rmark;
    private String dsName;
    private String status;
}
