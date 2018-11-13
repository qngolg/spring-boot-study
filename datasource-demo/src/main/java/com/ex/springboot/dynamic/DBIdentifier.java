package com.ex.springboot.dynamic;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类，用于区分各个线程连接的数据源
 *
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
public class DBIdentifier {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    //数据源名称容器 与DynamicDataSource中的dataSourceMap 数据源容器的key对应，用于数据源的切换
    public static List<String> dataSourceIds = new ArrayList<>();

    public static void setDataSourceType(String dataSourceType) {
        threadLocal.set(dataSourceType);
    }

    public static String getDataSourceType() {
        return threadLocal.get();
    }

    public static void clearDataSourceType() {
        threadLocal.remove();
    }

    public static boolean containsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }

}
