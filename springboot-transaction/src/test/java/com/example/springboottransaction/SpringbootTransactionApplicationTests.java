package com.example.springboottransaction;

import com.example.springboottransaction.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootTransactionApplicationTests {


    @Autowired
    UserService userService;

    @Test
    public void contextLoads() throws Exception {

        //事务不回滚
//        userService.saveWithoutRollBack("AAA");
        //事务回滚 事务虽然回滚 但是依然在数据库中查入了数据，只是把数据删除掉了
        userService.saveWithRollBack("AAA");

    }



}
