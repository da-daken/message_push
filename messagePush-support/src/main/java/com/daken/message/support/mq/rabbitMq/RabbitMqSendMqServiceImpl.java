package com.daken.message.support.mq.rabbitMq;

import com.daken.message.support.constant.MessageQueuePipeline;
import com.daken.message.support.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "daken.mq.pipeline", havingValue = MessageQueuePipeline.RABBIT_Mq)
public class RabbitMqSendMqServiceImpl implements SendMqService {
    @Override
    public void send(String topic, String jsonValue, String tagId) {

    }

    @Override
    public void send(String topic, String jsonValue) {

    }
}
