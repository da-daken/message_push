package com.daken.message.service.api.impl.action;

import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import org.springframework.stereotype.Service;

/**
 * 后置参数检查
 */
@Service
public class AfterParamCheckAction implements BusinessProcess {
    @Override
    public void process(ProcessContext content) {

    }
}
