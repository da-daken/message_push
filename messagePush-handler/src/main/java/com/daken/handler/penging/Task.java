package com.daken.handler.penging;

import com.daken.message.common.domain.TaskInfo;
import lombok.Data;
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

    @Override
    public void run() {

    }
}
