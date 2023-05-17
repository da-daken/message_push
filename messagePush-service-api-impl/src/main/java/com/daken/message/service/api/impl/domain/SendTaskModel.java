package com.daken.message.service.api.impl.domain;

import com.daken.message.common.domain.TaskInfo;
import com.daken.message.service.api.domain.MessageParam;
import com.daken.message.support.domain.MessageTemplate;
import com.daken.message.support.pipeline.ProcessModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author daken
 *
 * 目前一个发送消息的任务，之后还可增加
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendTaskModel implements ProcessModel {
    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 请求参数(用做list用于单个和多个消息同时使用)
     */
    private List<MessageParam> messageParamList;

    /**
     * 发送任务的信息
     */
    private List<TaskInfo> taskInfo;

    /**
     * 撤回任务的信息
     */
    private MessageTemplate messageTemplate;

}
