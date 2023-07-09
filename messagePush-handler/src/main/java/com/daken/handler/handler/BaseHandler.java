package com.daken.handler.handler;

import com.daken.handler.flowcontrol.FlowControlParam;
import com.daken.handler.flowcontrol.FlowControlService;
import com.daken.message.common.domain.AnchorInfo;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.AnchorState;
import com.daken.message.support.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author daken
 *
 * 作为发送消息的基础模版类
 */
@Slf4j
public abstract class BaseHandler implements Handler{
    /**
     * 渠道ID
     */
    protected Integer channelCode;

    /**
     * 限流参数
     */
    protected FlowControlParam flowControlParam;

    @Autowired
    private FlowControlService flowControlService;

    @Resource
    private HandlerHolder holder;

    @Autowired
    private LogUtils logUtils;

    @PostConstruct
    private void init(){
        holder.putHandler(channelCode, this);
    }

    /**
     * 流量控制
     * @param taskInfo
     */
    private void flowControl(TaskInfo taskInfo){
        // 子类设置了限流参数，才生效
        if(Objects.nonNull(flowControlParam)){
            flowControlService.flowControl(taskInfo, flowControlParam);
        }
    }

    public abstract boolean handler(TaskInfo taskInfo);

    @Override
    public void doHandler(TaskInfo taskInfo) {
        flowControl(taskInfo);
        if(handler(taskInfo)){
            logUtils.print(AnchorInfo.builder().state(AnchorState.SEND_SUCCESS.getCode()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
            return ;
        }
        logUtils.print(AnchorInfo.builder().state(AnchorState.SEND_FAIL.getCode()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
    }
}
