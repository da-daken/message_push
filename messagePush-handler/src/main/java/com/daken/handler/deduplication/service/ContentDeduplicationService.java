package com.daken.handler.deduplication.service;

import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.DeduplicationType;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author daken
 *
 * 限制每天发送都条数,计数去重
 */
@Service
public class ContentDeduplicationService extends AbstractDeduplicationService{
    public ContentDeduplicationService(){
        deduplicationType = DeduplicationType.CONTENT.getCode();
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
