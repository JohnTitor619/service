package com.yh.cms.handler.mq;

import com.alibaba.fastjson.JSON;
import com.yh.cms.handler.service.CmsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PostPageCustomer {
    @Autowired
    private CmsService cmsService;

    @RabbitListener(queues = "${yh.mq.queue}")
    public void postPage(String msg) {
        Map map = JSON.parseObject(msg, Map.class);
        if (map != null){
            String pageId = map.get("pageId").toString();
            cmsService.postPage(pageId);
        }
    }
}
