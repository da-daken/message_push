package com.daken.handler.receiver.rocketMq;

import com.alibaba.fastjson.JSON;
import com.daken.handler.receiver.ConsumeService;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.support.constant.MessageQueuePipeline;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Autowired
    private ConsumeService consumeService;

    @Override
    public void onMessage(String message) {
        if(StringUtils.isBlank(message)){
            return ;
        }
        List<TaskInfo> taskInfoList = JSON.parseArray(message, TaskInfo.class);
        consumeService.consume2Send(taskInfoList);
    }
}
