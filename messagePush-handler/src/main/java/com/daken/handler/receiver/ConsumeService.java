package com.daken.handler.receiver;

import com.daken.message.common.domain.TaskInfo;
import com.daken.message.support.domain.MessageTemplate;

import java.util.List;

/**
 * @author daken
 *
 * 消费MQ消息
 */
public interface ConsumeService {
    /**
     * 消费发送消息
     * @param taskInfoList
     */
    void consume2Send(List<TaskInfo> taskInfoList);

    /**
     * 消费撤回消息
     * @param messageTemplate
     */
    void consume2Recall(MessageTemplate messageTemplate);
}
