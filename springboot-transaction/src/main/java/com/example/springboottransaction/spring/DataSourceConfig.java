package com.example.springboottransaction.spring;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/27.
 */
@Configuration
@MapperScan(basePackages = {DataSourceConfig.PACKAGE},sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {

    static final String PACKAGE = "com.example.springboottransaction.mapper";
    static final String MAPPER_LOCATION = "classpath*:/mapper/*.xml";

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Bean(name="dataSource")
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setMaxActive(20);
        druidDataSource.setInitialSize(10);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        druidDataSource.setMinIdle(10);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setValidationQuery("SELECT 1");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setTestOnBorrow(false);
        try {
            druidDataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager baseaccountTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory baseaccountSqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(DataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
