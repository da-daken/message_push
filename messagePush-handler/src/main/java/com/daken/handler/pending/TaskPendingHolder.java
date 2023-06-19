package com.daken.handler.pending;

import com.daken.handler.config.HandlerThreadPool;
import com.daken.handler.utils.GroupIdMappingUtils;
import com.daken.message.support.utils.ThreadPoolUtils;
import com.dtp.core.thread.DtpExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author daken
 *
 * 将每个groupId都绑定一个线程池
 */
@Component
public class TaskPendingHolder {
    private HashMap<String, DtpExecutor> taskPendingHolder = new HashMap<>(32);

    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    /**
     * 给每个渠道分配一个线程池
     */
    @PostConstruct
    public void init(){
        /**
         * 优化：可以通过apollo进行配置
         */
        List<String> groupIds = GroupIdMappingUtils.getAllGroupIds();
        for (String groupId : groupIds){
            DtpExecutor executor = HandlerThreadPool.getExecutor(groupId);
            threadPoolUtils.register(executor);
            taskPendingHolder.put(groupId, executor);
        }
    }

    public DtpExecutor getExecutor(String groupId){
        return taskPendingHolder.get(groupId);
    }
}
