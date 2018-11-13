package com.ex.springboot.aop;

import com.ex.springboot.domain.DataSourceInfo;
import com.ex.springboot.dynamic.DBIdentifier;
import com.ex.springboot.dynamic.DDSRegister;
import com.ex.springboot.dynamic.DynamicDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author qiangl
 * Created by qgl on 2018/8/8.
 */
@Aspect
@Order(-10)
@Component
public class DDSAspect {

    private static final Logger logger = LoggerFactory.getLogger(DDSAspect.class);

    @Pointcut("execution(* com.ex.springboot.mapper.*.*(..)) ")
    public void excude() {
    }

    //默认取第一个参数为dsname
    @Before("excude()")
    public void changeDataSource(JoinPoint point) {

        Object obj = point.getArgs()[0];
        try {

            if (obj instanceof String) {
                String dsName = obj.toString();
                //现在当前数据源容器中寻找dsName
                if (!DBIdentifier.containsDataSource(dsName)) {
                    //从主数据库中去获取该数据源
                    List<DataSourceInfo> infos = DDSRegister.getDatasourceInfos(dsName);
                    if (!infos.isEmpty()) {
                        System.out.println("找到了数据源：" + dsName);
                        //构建数据源
                        DataSource dataSource = DDSRegister.buildDataSource(getDBMap(infos));
                        //获得路由数据源
                        Map<Object, Object> dBMap = DynamicDataSource.getInstance().getDataSourceMap();
                        //向路由数据源容器中添加这个数据源
                        dBMap.put(dsName, dataSource);
                        //重新加载数据源配置
                        DynamicDataSource.getInstance().setTargetDataSources(dBMap);
                        //设置当前线程的数据源
                        DBIdentifier.setDataSourceType(dsName);
                    } else {
                        //在数据源中没有找到该数据源
                        logger.error("数据源[{}]不存在", obj);
                    }

                } else {//在数据源容器中找到了该数据源，给当前线程切换该数据源  getSignature返回目标方法的签名
                    logger.error("Use DataSource: {} > {}", dsName, point.getSignature());
                }
                logger.info("-----------args DataSource : {} > {}", dsName, point.getSignature());
            } else {
                logger.info("使用默认数据源");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @After("excude()")
    public void restoreDataSource(JoinPoint point) {
        Object obj = point.getArgs()[0];
        logger.info("Revert DataSource : {} > {}", obj == null ? "dataSource" : obj.toString(), point.getSignature());
        //清除线程数据源痕迹
        DBIdentifier.clearDataSourceType();
    }


    //组建该数据源Map
    public static Map<String, Object> getDBMap(List<DataSourceInfo> infos) {
        DataSourceInfo info = infos.get(0);
        Map<String, Object> dsMap = new HashMap<>();
        dsMap.put("type", info.getType());
        dsMap.put("driver-class-name", info.getDriverClassName());
        dsMap.put("url", info.getUrl());
        dsMap.put("username", info.getUsername());
        dsMap.put("password", info.getPassword());
        return dsMap;
    }


}
