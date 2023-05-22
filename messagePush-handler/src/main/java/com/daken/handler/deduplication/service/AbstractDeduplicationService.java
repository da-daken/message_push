package com.daken.handler.deduplication.service;

import cn.hutool.core.collection.CollUtil;
import com.daken.handler.deduplication.DeduplicationHolder;
import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.DeduplicationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * 模版方法模式 构建去重服务
 */
@Slf4j
public abstract class AbstractDeduplicationService implements DeduplicationService {
    protected Integer deduplicationType;
    @Autowired
    private DeduplicationHolder holder;

    @PostConstruct
    public void init(){
        holder.putService(deduplicationType, this);
    }

    /**
     * 构建去重key
     * @param taskInfo
     * @param receiver
     * @return
     */
    public abstract String deduplicationSingleKey(TaskInfo taskInfo, String receiver);

    /**
     * 利用去重key进行过滤
     * @param taskInfo
     * @param param
     * @return
     */
    public abstract Set<String> limitFilter(TaskInfo taskInfo, DeduplicationParam param);

    @Override
    public void deduplication(DeduplicationParam param) {
        TaskInfo taskInfo = param.getTaskInfo();
        // 1. 构建去重key
        // 2. 利用去重key进行过滤
        Set<String> filterReceiver = limitFilter(taskInfo, param);
        // 3. 剔除符合条件的用户
        if(CollUtil.isNotEmpty(filterReceiver)){
            taskInfo.getReceiver().removeAll(filterReceiver);
            log.info("去重类型：{} 需要去重的用户:{}", param.getAnchorState(), filterReceiver);
        }
    }
}
