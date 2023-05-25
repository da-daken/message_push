package com.daken.handler.penging;

import cn.hutool.core.collection.CollUtil;
import com.daken.handler.deduplication.DeduplicationRuleService;
import com.daken.handler.handler.BaseHandler;
import com.daken.handler.shield.ShieldService;
import com.daken.message.common.domain.TaskInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author daken
 *
 * 处理消息的任务
 * 1. 是否丢弃（待定）
 * 2. 屏蔽消息
 * 3. 消息去重
 * 4. 发送消息给接收者
 */
@Data
@Component
public class Task implements Runnable{

    private TaskInfo taskInfo;

    @Autowired
    private ShieldService shieldService;
    @Autowired
    private DeduplicationRuleService deduplicationRuleService;
    @Autowired
    private BaseHandler handler;
    @Override
    public void run() {
        // 1. 屏蔽消息
        shieldService.shield(taskInfo);
        // 2. 消息去重
        if(CollUtil.isNotEmpty(taskInfo.getReceiver())){
            deduplicationRuleService.deduplication(taskInfo);
        }
        // todo 3. 真正发送消息
        if(CollUtil.isNotEmpty(taskInfo.getReceiver())){

        }
    }
}
