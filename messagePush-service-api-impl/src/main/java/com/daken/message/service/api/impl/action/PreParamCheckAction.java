package com.daken.message.service.api.impl.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.daken.message.common.constant.OnlyConstant;
import com.daken.message.common.enums.RespStatusEnum;
import com.daken.message.common.vo.BasicResultVO;
import com.daken.message.service.api.domain.MessageParam;
import com.daken.message.service.api.impl.domain.SendTaskModel;
import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author daken
 *
 * 前置参数检查(检查模版ID和接收者是否ok)
 */
@Slf4j
@Service
public class PreParamCheckAction implements BusinessProcess {
    @Override
    public void process(ProcessContext context) {
        SendTaskModel sendTaskModel = (SendTaskModel) context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();

        // 1. 没有传入 消息模版ID 或者 消息参数
        if(Objects.isNull(messageTemplateId) || CollUtil.isEmpty(messageParamList)){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            log.error("消息模版ID或者消息参数为空");
            return ;
        }
        // 2. 过滤 receiver=null 的消息参数
        List<MessageParam> resultMessageParamList = messageParamList.stream()
                .filter(messageParam -> !StrUtil.isBlank(messageParam.getReceiver()))
                .collect(Collectors.toList());
        if(CollUtil.isEmpty(resultMessageParamList)){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            log.info("接收者为空");
            return ;
        }
        // 3. 不能超过100个 receiver
        if(resultMessageParamList.stream().anyMatch(messageParam -> messageParam.getReceiver().split(StrUtil.COMMA).length > OnlyConstant.BATCH_RECEIVER_SIZE)){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TOO_MANY_RECEIVER));
            log.info("请求的接收者大于100个");
            return ;
        }

        sendTaskModel.setMessageParamList(resultMessageParamList);
        context.setProcessModel(sendTaskModel);
    }
}
