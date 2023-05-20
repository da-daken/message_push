package com.daken.handler.receiver;

import com.daken.message.common.domain.TaskInfo;
import com.daken.message.support.domain.MessageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumeServiceImpl implements ConsumeService {
    @Autowired
    private ApplicationContext context;

    @Override
    public void consume2Send(List<TaskInfo> taskInfoList) {

    }

    @Override
    public void consume2Recall(MessageTemplate messageTemplate) {

    }
}
