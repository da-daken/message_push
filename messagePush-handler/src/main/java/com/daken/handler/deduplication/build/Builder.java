package com.daken.handler.deduplication.build;

import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.domain.TaskInfo;

public interface Builder {
    /**
     * 构建 deduplicationParam 参数
     * @param deduplicationConfig
     * @param taskInfo
     * @return
     */
    DeduplicationParam getParamsFromConfig(String deduplicationConfig, TaskInfo taskInfo);
}
