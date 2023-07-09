package com.daken.message.service.api.impl.service;

import cn.monitor4all.logRecord.annotation.OperationLog;
import com.daken.message.common.enums.RespStatusEnum;
import com.daken.message.common.vo.BasicResultVO;
import com.daken.message.service.api.domain.BatchSendRequest;
import com.daken.message.service.api.domain.SendRequest;
import com.daken.message.service.api.domain.SendResponse;
import com.daken.message.service.api.impl.domain.SendTaskModel;
import com.daken.message.service.api.service.SendService;
import com.daken.message.support.pipeline.ProcessContext;
import com.daken.message.support.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
public class SendServiceImpl implements SendService {
    @Autowired
    private ProcessController processController;

    @Override
    @OperationLog(bizType = "SendService#send", bizId = "#sendRequest.messageTemplateId", msg = "#sendRequest")
    public SendResponse send(SendRequest sendRequest) {
        //对 sendRequest 判空，防止后面空指针
        //因为这个接口很容易被发现，防止有人搞破坏
        if(Objects.isNull(sendRequest)){
            return new SendResponse(RespStatusEnum.CLIENT_BAD_PARAMETERS.getCode(), RespStatusEnum.CLIENT_BAD_PARAMETERS.getMsg());
        }

        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .messageParamList(Collections.singletonList(sendRequest.getMessageParam()))
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(sendRequest.getCode())
                .needBreak(false)
                .processModel(sendTaskModel)
                .response(BasicResultVO.success())
                .build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }

    @Override
    @OperationLog(bizType = "SendService#batchSend", bizId = "#batchSendRequest.messageTemplateId", msg = "#batchSendRequest")
    public SendResponse batchSend(BatchSendRequest batchSendRequest) {
        if(Objects.isNull(batchSendRequest)){
            return new SendResponse(RespStatusEnum.CLIENT_BAD_PARAMETERS.getCode(), RespStatusEnum.CLIENT_BAD_PARAMETERS.getMsg());
        }

        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(batchSendRequest.getMessageTemplateId())
                .messageParamList(batchSendRequest.getMessageParamList())
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(batchSendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success())
                .build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }
}
