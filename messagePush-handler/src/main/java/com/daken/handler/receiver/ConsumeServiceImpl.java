package com.daken.handler.receiver;

import cn.hutool.core.collection.CollUtil;
import com.daken.handler.handler.HandlerHolder;
import com.daken.handler.pending.Task;
import com.daken.handler.pending.TaskPendingHolder;
import com.daken.handler.utils.GroupIdMappingUtils;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.support.domain.MessageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumeServiceImpl implements ConsumeService {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private TaskPendingHolder taskPendingHolder;

    @Autowired
    private HandlerHolder handlerHolder;

    @Override
    public void consume2Send(List<TaskInfo> taskInfoLists) {
        String topicGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));
        for (TaskInfo taskInfo : taskInfoLists){
            Task task = context.getBean(Task.class);
            task.setTaskInfo(taskInfo);
            taskPendingHolder.getExecutor(topicGroupId).execute(task);
        }
    }

    @Override
    public void consume2Recall(MessageTemplate messageTemplate) {
        handlerHolder.getHandler(messageTemplate.getSendChannel()).recall(messageTemplate);
    }
}
