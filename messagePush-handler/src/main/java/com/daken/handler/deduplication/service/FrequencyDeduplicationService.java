package com.daken.handler.deduplication.service;

import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.DeduplicationType;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author daken
 *
 * 频次去重（滑动窗口去重，使用redis中zset数据结构，可以做到严格控制单位时间内的频次）
 */
@Service
public class FrequencyDeduplicationService extends AbstractDeduplicationService {
    public FrequencyDeduplicationService(){
        deduplicationType = DeduplicationType.FREQUENCY.getCode();
    }
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        // todo
        return null;
    }

    @Override
    public Set<String> limitFilter(TaskInfo taskInfo, DeduplicationParam param) {
        // todo
        return null;
    }
}
