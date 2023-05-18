package com.daken.message.service.api.impl.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.IdType;
import com.daken.message.service.api.impl.domain.SendTaskModel;
import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author daken
 *
 * 后置参数检查(检查接收者是否合法)
 */
@Slf4j
@Service
public class AfterParamCheckAction implements BusinessProcess {
    public static final String PHONE_REGEX_EXP = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[0-9])|(18[0-9])|(19[1,8,9]))\\d{8}$";
    public static final String EMAIL_REGEX_EXP = "^[A-Za-z0-9-_\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    public static final HashMap<Integer, String> CHANNEL_REGEX_EXP = new HashMap<>();

    static {
        CHANNEL_REGEX_EXP.put(IdType.PHONE.getCode(), PHONE_REGEX_EXP);
        CHANNEL_REGEX_EXP.put(IdType.EMAIL.getCode(), EMAIL_REGEX_EXP);
    }


    @Override
    public void process(ProcessContext content) {
        SendTaskModel sendTaskModel = (SendTaskModel) content.getProcessModel();
        List<TaskInfo> taskInfo = sendTaskModel.getTaskInfo();
        // 过滤接收者
        filterIllegalReceiver(taskInfo);

    }

    /**
     * 利用正则表达式过滤接收者是否合法
     *
     * @param taskInfo
     */
    private void filterIllegalReceiver(List<TaskInfo> taskInfo) {
        Integer idType = CollUtil.getFirst(taskInfo).getIdType();
        Iterator<TaskInfo> iterator = taskInfo.iterator();
        while(iterator.hasNext()){
            TaskInfo task = iterator.next();
            Set<String> illegalReceiver = task.getReceiver().stream()
                    .filter(receiver -> !ReUtil.isMatch(CHANNEL_REGEX_EXP.get(idType), receiver))
                    .collect(Collectors.toSet());
            if(!CollUtil.isEmpty(illegalReceiver)){
                task.getReceiver().removeAll(illegalReceiver);
                log.info("模版ID:{} 存在不合法接收者:{}", task.getMessageTemplateId(), JSON.toJSONString(illegalReceiver));
            }
            // 如果去除不合法后接收者全部没了
            // 就删除这个taskInfo
            if(CollUtil.isEmpty(task.getReceiver())){
                iterator.remove();
            }
        }
    }
}
