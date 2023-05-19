package com.daken.message.service.api.impl.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.daken.message.common.enums.RespStatusEnum;
import com.daken.message.common.vo.BasicResultVO;
import com.daken.message.service.api.enums.BusinessCode;
import com.daken.message.service.api.impl.domain.SendTaskModel;
import com.daken.message.support.mq.SendMqService;
import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author daken
 *
 * 发送消息到 mq 中
 */
@Service
@Slf4j
public class SendMqAction implements BusinessProcess {
    @Autowired
    private SendMqService sendMqService;

    @Value("${daken.business.topic.name}")
    private String sendMessageTopic;

    @Value("${daken.business.recall.topic.name}")
    private String recall;

    @Value("${daken.business.tagId.value}")
    private String tagId;

    @Value("${daken.mq.pipeline}")
    private String mqPipeline;

    @Override
    public void process(ProcessContext context) {
        SendTaskModel sendTaskModel = (SendTaskModel) context.getProcessModel();
        try {
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) {
                String message = JSON.toJSONString(sendTaskModel.getTaskInfo(), new SerializerFeature[]{SerializerFeature.WriteClassName});
                sendMqService.send(sendMessageTopic, message, tagId);
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) {
                String message = JSON.toJSONString(sendTaskModel.getMessageTemplate(), new SerializerFeature[]{SerializerFeature.WriteClassName});
                sendMqService.send(recall, message, tagId);
            }
        } catch (Exception e){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("发送mq {} 失败!! e:{}", mqPipeline, Throwables.getStackTraceAsString(e));
        }
    }
}
