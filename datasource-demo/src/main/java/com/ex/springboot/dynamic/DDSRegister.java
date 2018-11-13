package com.ex.springboot.dynamic;

import com.ex.springboot.domain.DataSourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态数据源注册类
 *
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
public class DDSRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {


    private static final Logger logger = LoggerFactory.getLogger(DDSRegister.class);
    private static final Object DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";
    //主数据源
    private static DataSource defaultDatasource;
    //数据源管理
    private static Map<String, DataSource> customDataSources = new HashMap<>();

    @Override
    public void setEnvironment(Environment environment) {
        //初始化默认数据源
        initDefaultDataSource(environment);
        //初始化其他数据源
        intDataSourceMap(environment);
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<Object, Object> targetDataSource = new HashMap<>();

        targetDataSource.put("adminDatasource", defaultDatasource);
        //将数据源存入数据源名称容器
        DBIdentifier.dataSourceIds.add("adminDatasource");
        //添加更多数据源
        targetDataSource.putAll(customDataSources);
        for (String key : customDataSources.keySet()) {
            DBIdentifier.dataSourceIds.add(key);
        }
        //创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDatasource);
        mpv.addPropertyValue("targetDataSources", targetDataSource);
        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
        logger.info("Dynamic DataSource Registry");
    }


    /**
     * 初始化多个副数据源
     *
     * @param environment
     */
    private void intDataSourceMap(Environment environment) {
        // 读取库表中datasource获取更多数据源
        Map<String, Map<String, Object>> customInfo = getCustomDataSourceInfo(null);
        for (String key : customInfo.keySet()) {
            Map<String, Object> dsMap = customInfo.get(key);
            DataSource ds = buildDataSource(dsMap);
            customDataSources.put(key, ds);
        }
    }

    /**
     * 初始化默认数据源：主数据源
     *
     * @param env
     */
    private void initDefaultDataSource(Environment env) {
        Map<String, Object> dsMap = new HashMap<>();
        dsMap.put("type", env.getProperty("spring.datasource.default.type"));
        dsMap.put("driver-class-name", env.getProperty("spring.datasource.default.driver-class-name"));
        dsMap.put("url", env.getProperty("spring.datasource.default.url"));
        dsMap.put("username", env.getProperty("spring.datasource.default.username"));
        dsMap.put("password", env.getProperty("spring.datasource.default.password"));
        defaultDatasource = buildDataSource(dsMap);
    }

    /**
     * 创建DataSource
     *
     * @param dsMap
     * @return
     */
    public static DataSource buildDataSource(Map<String, Object> dsMap) {
        try {
            Object type = dsMap.get("type");
            if (type == null)
                type = DATASOURCE_TYPE_DEFAULT;

            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            String driverClassName = dsMap.get("driver-class-name").toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();

            DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName)
                    .type(dataSourceType).url(url).username(username).password(password);
            return factory.build();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 从主数据库中获取数据库信息
     *
     * @param dsName
     * @return
     */
    public static List<DataSourceInfo> getDatasourceInfos(String dsName) {
        String sql = "select type,`driver_class_name`,url,username,`password`,`dsname` from datasource where status=1";
        if (dsName != null) {
            sql += " and dsname='" + dsName + "'";
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(defaultDatasource);
        List<DataSourceInfo> infos = jdbcTemplate.query(sql, new RowMapper<DataSourceInfo>() {
            @Override
            public DataSourceInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DataSourceInfo info = new DataSourceInfo();
                info.setType(rs.getString("type"));
                info.setDriverClassName(rs.getString("driver_class_name"));
                info.setUrl(rs.getString("url"));
                info.setPassword(rs.getString("password"));
                info.setUsername(rs.getString("username"));
                info.setDsName(rs.getString("dsname"));
                return info;
            }
        });
        return infos;
    }

    /**
     * 构建多个数据源容器Map<String, Map<String, Object>>
     *
     * @param dsName
     * @return
     */
    public Map<String, Map<String, Object>> getCustomDataSourceInfo(String dsName) {
        Map<String, Map<String, Object>> customMap = new HashMap<>();
        List<DataSourceInfo> infos = getDatasourceInfos(dsName);
        for (DataSourceInfo info : infos) {
            Map<String, Object> dsMap = new HashMap<>();
            dsMap.put("type", info.getType());
            dsMap.put("driver-class-name", info.getDriverClassName());
            dsMap.put("url", info.getUrl());
            dsMap.put("username", info.getUsername());
            dsMap.put("password", info.getPassword());
            customMap.put(info.getDsName(), dsMap);
        }
        return customMap;
    }
}
