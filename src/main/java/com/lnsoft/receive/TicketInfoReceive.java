package com.lnsoft.receive;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 需要消息确认回执，和消息失败重发：
 * 接收从topic.ticketRespInfo队列的数据（主要存放服务端订单查询的结果）
 * Created By Chr on 2019/2/12/0012.
 */
@Component
public class TicketInfoReceive {
    /**
     * 监听队列：topic.orderReceive
     * <p>
     * 该监听作用：监听客户端的订单系统出票，成功之后，客户端会把成功的数据放入到topic.ticketRespInfo队列中，
     * 由消费者去该队列拉取数据，查看是否出票成功
     *
     * @param ticketInfo
     */
    @RabbitListener(queues = "topic.ticketRespInfo")//客户端监听服务端的队列，有消息就拉取共客户端使用
    @RabbitHandler
    public void process1(String ticketInfo, Message message, Channel channel) throws IOException {
        try {

            //###############################################
            //模拟消费者从服务器出票队列中拉取数据，查看是否出票成功
            System.out.println("服务器订单系统和出票系统已经操作完毕，出票成功！！===response from 12306 ticketInfo is===" + ticketInfo+"===");
            //###############################################

            //接到消息确认回执
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // false只确认当前一个消息收到，true确认所有consumer获得的消息

        } catch (Exception e) {
            e.printStackTrace();

            if (message.getMessageProperties().getRedelivered()) {//该消息已经被消费
                System.out.println("消息已重复处理失败,拒绝再次接收...");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true); // 拒绝消息
            } else {//废弃该消息，是为消费者未消费，消息从新返回queue
                System.out.println("消息即将再次返回队列处理...");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true); // requeue为是否重新回到队列
            }

        }
    }


}
