package com.daken.message.service.api.impl.action;

import com.daken.message.service.api.impl.domain.SendTaskModel;
import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import com.daken.message.support.pipeline.ProcessModel;
import org.springframework.stereotype.Service;

/**
 * @author daken
 *
 * 发送消息到 mq 中
 */
@Service
public class SendMqAction implements BusinessProcess {
    @Override
    public void process(ProcessContext content) {
        SendTaskModel sendTaskModel = (SendTaskModel) content.getProcessModel();

    }
}
