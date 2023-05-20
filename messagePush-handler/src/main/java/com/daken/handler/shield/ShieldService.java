package com.daken.handler.shield;

import com.daken.message.common.domain.TaskInfo;

/**
 * @author daken
 *
 * 屏蔽消息时间段
 */
public interface ShieldService {


    void shield(TaskInfo taskInfo);
}
