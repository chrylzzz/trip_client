package com.lnsoft;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

/**
 * 无MQ测试
 * Created By Chr on 2019/2/11/0011.
 */
@SpringBootTest(classes = TripClientApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestInvokeRemote {

//    public static void main(String args[]) throws InterruptedException {
//        new TestInvokeRemote().TestInvoke();
//    }

    private final String url = "http://127.0.0.1:8091/buyTicket?idcard=123456";

    //底层封装了HttpClient
    RestTemplate restTemplate = new RestTemplate();

    //并发量
    private static final int USER_NUMBERS = 200;//200-10000
    //CountDownLatch 并发用的多，测试并发的神器
    private static CountDownLatch cdl = new CountDownLatch(USER_NUMBERS);

    /**
     * 无mq模拟并发
     *
     * @throws InterruptedException
     */
    @Test
    public void TestInvoke() throws InterruptedException {
        for (int i = 0; i < USER_NUMBERS; i++) {

            new Thread(new TicketRequest()).start();//此处不是并发
            //第一次200，第二次199，第三次198......0，直到减到0，会把等待的同时去执行：调用操作
            cdl.countDown();
        }
    }

    /**
     * 无mq模拟高并发
     */
    //模拟多线程
    public class TicketRequest implements Runnable {

        @Override
        public void run() {
            try {
                //等待操作，直到cdl为0的时候，所有的线程全部唤醒，才去执行：调用操作
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //调用操作
            String body = restTemplate.getForEntity(url, String.class).getBody();
            System.err.println(body);
        }
    }
}
