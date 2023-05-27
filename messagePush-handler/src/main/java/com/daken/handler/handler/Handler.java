package com.daken.handler.handler;

import com.daken.message.common.domain.TaskInfo;
import com.daken.message.support.domain.MessageTemplate;

/**
 * 处理消息接口
 */
public interface Handler {
    /**
     * 发送消息
     * @param taskInfo
     */
    void doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     * @param messageTemplate
     */
    void recall(MessageTemplate messageTemplate);
}
