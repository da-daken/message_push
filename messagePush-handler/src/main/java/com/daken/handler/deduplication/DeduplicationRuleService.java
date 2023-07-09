package com.daken.handler.deduplication;

import com.daken.message.common.constant.CommonConstant;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.DeduplicationType;
import com.daken.message.common.enums.EnumUtil;
import com.daken.message.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DeduplicationRuleService {
    public static final String DEDUPLICATION_RULE_KEY = "deduplicationRule";

    @Autowired
    private ConfigService config;
    @Autowired
    private DeduplicationHolder holder;

    public void deduplication(TaskInfo taskInfo){
        // 获取配置
        // 配置样例：{"deduplication_10":{"num":1,"time":300},"deduplication_20":{"num":5}}
        String deduplicationConfig = config.getProperty(DEDUPLICATION_RULE_KEY, CommonConstant.EMPTY_JSON_OBJECT);
        // 可能有多种去重服务
        // 所以都遍历一遍
        List<Integer> deduplicationCodeList = EnumUtil.getCodeList(DeduplicationType.class);
        for (Integer deduplicationType : deduplicationCodeList){
            DeduplicationParam deduplicationParam = holder.getBuilderHashMap(deduplicationType).getParamsFromConfig(deduplicationConfig, taskInfo);
            if(Objects.nonNull(deduplicationParam)){
                holder.getServiceHashMap(deduplicationType).deduplication(deduplicationParam);
            }
        }
    }
}
