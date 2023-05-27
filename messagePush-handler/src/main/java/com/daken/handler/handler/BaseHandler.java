package com.daken.handler.handler;

import com.daken.message.common.domain.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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

    @Resource
    private HandlerHolder holder;

    @PostConstruct
    private void init(){
        holder.putHandler(channelCode, this);
    }

    /**
     * 流量控制
     * @param taskInfo
     */
    private void flowControl(TaskInfo taskInfo){
        // todo
    }

    public abstract boolean handler(TaskInfo taskInfo);

    @Override
    public void doHandler(TaskInfo taskInfo) {
        flowControl(taskInfo);
        if(handler(taskInfo)){
            log.info("发送成功");
            return ;
        }
        log.info("发送失败");
    }
}
