package com.lnsoft.sender;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created By Chr on 2019/2/12/0012.
 */
@Component
public class RabbitSender {

    //注入AmqpTemlate Rest
    @Autowired
    private AmqpTemplate template;

    /**
     * 由AmqpTemlate 将数据发送到交换机和队列
     * @param exchange  ：交换器
     * @param routeKey  ：路由键
     * @param content   ：发送内容
     */
    public void sendTopic(String exchange,String routeKey,String content){
        //消费者下单车票到队列中：数据内容通过路由键，绑定到对应的交换器，
        template.convertAndSend(exchange,routeKey,content);
    }
}
