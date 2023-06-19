package com.daken.handler.flowcontrol;

import com.daken.message.common.domain.TaskInfo;

public interface FlowControlService {
    /**
     * 渠道初始化限流
     * @param flowControlParam
     * @return
     */
    void flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam);
}
