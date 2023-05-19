package com.daken.handler.receiver.rocketMq;

import com.daken.message.support.constant.MessageQueuePipeline;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author daken
 *
 * 消息接收
 */
@Component
@ConditionalOnProperty(name = "daken.mq.pipeline", havingValue = MessageQueuePipeline.ROCKET_MQ)
@RocketMQMessageListener(topic = "${daken.business.topic.name}",
        consumerGroup = "${daken.rocketmq.consumer.group}",
        selectorType = SelectorType.TAG,
        selectorExpression = "${daken.business.tagId.value}"
)
public class RocketMqReceiver implements RocketMQListener<String> {

    @Override
    public void onMessage(String s) {

    }
}
