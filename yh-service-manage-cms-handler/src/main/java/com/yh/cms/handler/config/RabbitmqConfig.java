package com.yh.cms.handler.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    //交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    public static final String EX_ROUTING_CMS_QUEUE="ex_routing_cms_queue";

    @Value("${yh.mq.queue}")
    private String queueName;

    @Value("${yh.mq.routeKey}")
    private String routeKey;
    //创建交换机对象
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange createExchange() {
        Exchange exchange = ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
        return exchange;
    }
    //创建队列
    @Bean(EX_ROUTING_CMS_QUEUE)
    public Queue createQueue() {
        Queue queue = new Queue(queueName);
        return queue;
    }

    //把队列和交换机进行绑定
    @Bean
    public Binding binding(@Qualifier(EX_ROUTING_CMS_QUEUE) Queue queue,
                           @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routeKey).noargs();
        return binding;
    }
}
