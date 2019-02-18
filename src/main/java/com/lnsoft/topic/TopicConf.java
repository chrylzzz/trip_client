package com.lnsoft.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 配置类，当系统启动，根据需求创建减缓其和队列，用来接收服务端发送过来的数据
 * </p>
 * Created By Chr on 2019/2/12/0012.
 */
@Configuration
public class TopicConf {
    //系统启动时，创建的message队列到rabbitMQ
    @Bean(name = "message")//队列名字：该队列是服务端提供的，服务端把数据发送到该队列中，供消费者消费
    public Queue queueMessage() {
        return new Queue("topic.ticket");
    }

    //系统启动时：创建一个exchange的交换器到rabbitMQ Map key value
    @Bean//交换器
    public TopicExchange exchange() {
        return new TopicExchange("exchange");
    }
    //系统启动时：将exchange的交换器其与队列绑定
    //将队列topic.ticket与exchange绑定，binding_key为topic.message，完全匹配

    //绑定，声明关系
    @Bean
    Binding bindingExchangeMessage(@Qualifier("message") Queue queueMessage, TopicExchange exchange) {
        //使用路由key将交换器和指定的队列绑定起来
        return BindingBuilder.bind(queueMessage).to(exchange).with("topic.ticket.routeKey");
    }
}
