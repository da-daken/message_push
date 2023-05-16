package com.daken.message.service.api.impl.action;

import com.daken.message.service.api.impl.domain.SendTaskModel;
import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 组装参数
 */
@Slf4j
@Service
public class AssembleAction implements BusinessProcess {
    @Override
    public void process(ProcessContext context) {
        SendTaskModel sendTaskModel = (SendTaskModel) context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();

        // 1. 根据模版ID查询是否存在

        // 2. 组装参数
    }
}
