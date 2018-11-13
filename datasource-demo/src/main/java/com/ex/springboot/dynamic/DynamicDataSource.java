package com.ex.springboot.dynamic;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * 路由数据源 extend AbstractRoutingDataSource
 *
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    //数据源容器
    private static Map<Object, Object> dataSourceMap = new HashMap<>();

    private static byte[] lock = new byte[0];

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        //将数据源存入数据源容器
        dataSourceMap.putAll(targetDataSources);
        super.afterPropertiesSet();//important 重载数据源
    }

    //通过DBIdentifier.getDataSourceType()来路由到相应的数据源
    @Override
    protected Object determineCurrentLookupKey() {
        return DBIdentifier.getDataSourceType();
    }

    public static Map<Object, Object> getDataSourceMap() {
        return dataSourceMap;
    }

    //私有构造 静态内部类单例
    private DynamicDataSource() {
    }

    private static class DynamicDataSourceBuilder {
        private static DynamicDataSource instance = new DynamicDataSource();
    }

    public static DynamicDataSource getInstance() {
        return DynamicDataSourceBuilder.instance;
    }

}
