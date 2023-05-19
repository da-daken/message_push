package com.daken.message.support.mq.kafka;

import com.daken.message.support.constant.MessageQueuePipeline;
import com.daken.message.support.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "daken.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
public class KafkaSendMqServiceImpl implements SendMqService {
    @Override
    public void send(String topic, String jsonValue, String tagId) {

    }

    @Override
    public void send(String topic, String jsonValue) {

    }
}
