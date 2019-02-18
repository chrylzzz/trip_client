package com.lnsoft;

import com.lnsoft.sender.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TripClientApplicationTests {
    //并发量
    private static final int USER_NUM = 5000;

    //倒计时器，用于模拟高并发
    private static CountDownLatch countDownLatch = new CountDownLatch(USER_NUM);

    @Autowired
    private RabbitSender rabbitSender;

    /**
     * 模拟并发
     */
    @Test
    public void contextLoads() throws InterruptedException {
        for (int i = 0; i < USER_NUM; i++) {

            new Thread(new UserRequest()).start();//此处不是并发
            //第一次200，第二次199，第三次198......0，直到减到0，会把等待的同时去执行：调用操作
            countDownLatch.countDown();
        }
        Thread.currentThread().sleep(1000);
    }

    /**
     * 内部类继承线程接口，模拟买票请求
     */
    public class UserRequest implements Runnable {

        @Override
        public void run() {
            try {
                countDownLatch.await();//当前线程等待，等所有的线程实例化后，同时停止等待后调用接口代码
            } catch (Exception e) {
                e.printStackTrace();
            }
            String exchange = "exchange";//交换器
            String routeKey = "topic.ticket.routeKey";//路由key
            String idcard = "123456";
            rabbitSender.sendTopic(exchange, routeKey, idcard);
        }
    }
}

