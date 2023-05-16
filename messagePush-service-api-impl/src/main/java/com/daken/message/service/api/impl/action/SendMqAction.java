package com.daken.message.service.api.impl.action;

import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import org.springframework.stereotype.Service;

/**
 * 发送消息到 mq 中
 */
@Service
public class SendMqAction implements BusinessProcess {
    @Override
    public void process(ProcessContext content) {

    }
}
