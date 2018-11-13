package com.example.springboottransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
//@EnableTransactionManagement //开启事务
public class SpringbootTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootTransactionApplication.class, args);
    }


}
