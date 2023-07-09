package com.daken.handler.receiver;

import cn.hutool.core.collection.CollUtil;
import com.daken.handler.handler.HandlerHolder;
import com.daken.handler.pending.Task;
import com.daken.handler.pending.TaskPendingHolder;
import com.daken.handler.utils.GroupIdMappingUtils;
import com.daken.message.common.domain.AnchorInfo;
import com.daken.message.common.domain.LogParam;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.AnchorState;
import com.daken.message.support.domain.MessageTemplate;
import com.daken.message.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumeServiceImpl implements ConsumeService {
    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    private static final String LOG_BIZ_RECALL_TYPE = "Receiver#recall";
    @Autowired
    private ApplicationContext context;

    @Autowired
    private TaskPendingHolder taskPendingHolder;

    @Autowired
    private HandlerHolder handlerHolder;

    @Autowired
    private LogUtils logUtils;

    @Override
    public void consume2Send(List<TaskInfo> taskInfoLists) {
        String topicGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));
        for (TaskInfo taskInfo : taskInfoLists){
            logUtils.print(LogParam.builder().bizType(LOG_BIZ_TYPE).object(taskInfo).build(), AnchorInfo.builder().ids(taskInfo.getReceiver()).businessId(taskInfo.getBusinessId()).state(AnchorState.RECEIVE.getCode()).build());
            Task task = context.getBean(Task.class);
            task.setTaskInfo(taskInfo);
            taskPendingHolder.getExecutor(topicGroupId).execute(task);
        }
    }

    @Override
    public void consume2Recall(MessageTemplate messageTemplate) {
        logUtils.print(LogParam.builder().bizType(LOG_BIZ_RECALL_TYPE).object(messageTemplate).build());
        handlerHolder.getHandler(messageTemplate.getSendChannel()).recall(messageTemplate);
    }
}
