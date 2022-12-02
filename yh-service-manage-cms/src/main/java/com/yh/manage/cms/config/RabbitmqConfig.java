package com.yh.manage.cms.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    //交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    //创建交换机对象
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange createExchange() {
        Exchange exchange = ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
        return exchange;

    }
}
