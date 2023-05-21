package com.daken.handler.deduplication.build;

import com.alibaba.fastjson.JSONObject;
import com.daken.handler.deduplication.DeduplicationHolder;
import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.domain.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Objects;

public abstract class AbstractDeduplicationParamBuilder implements Builder {
    public static final String DEDUPLICATION_CONFIG_PRE = "deduplication_";
    protected Integer deduplicationType;

    @Autowired
    private DeduplicationHolder holder;
    @PostConstruct
    public void init(){
        holder.putBuilder(deduplicationType, this);
    }

    /**
     * 构造参数，和设置埋点
     * @param jsonObject
     * @return
     */
    public abstract DeduplicationParam build(JSONObject jsonObject);

    @Override
    public DeduplicationParam getParamsFromConfig(String deduplicationConfig, TaskInfo taskInfo) {
        JSONObject jsonObject = JSONObject.parseObject(deduplicationConfig);
        if(Objects.isNull(jsonObject)){
            return null;
        }
        DeduplicationParam deduplicationParam = build(jsonObject);
        deduplicationParam.setTaskInfo(taskInfo);
        return deduplicationParam;
    }
}
