package com.daken.handler.receiver.rocketMq;

import com.alibaba.fastjson.JSON;
import com.daken.handler.receiver.ConsumeService;
import com.daken.message.support.constant.MessageQueuePipeline;
import com.daken.message.support.domain.MessageTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "daken.mq.pipeline", havingValue = MessageQueuePipeline.ROCKET_MQ)
@RocketMQMessageListener(topic = "${daken.business.recall.topic.name}",
        consumerGroup = "${daken.rocketmq.recall.consumer.group}",
        selectorType = SelectorType.TAG,
        selectorExpression = "${daken.business.tagId.value}"
)
public class RocketMqRecallReceiver implements RocketMQListener<String> {
    @Autowired
    private ConsumeService consumeService;

    @Override
    public void onMessage(String message) {
        if(StringUtils.isBlank(message)){
            return ;
        }
        MessageTemplate messageTemplate = JSON.parseObject(message, MessageTemplate.class);
        consumeService.consume2Recall(messageTemplate);
    }
}
