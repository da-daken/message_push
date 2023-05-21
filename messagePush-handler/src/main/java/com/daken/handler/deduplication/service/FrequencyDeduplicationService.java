package com.daken.handler.deduplication.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.constant.CommonConstant;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.DeduplicationType;
import com.daken.message.support.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daken
 *
 * 渠道去重
 */
@Service
public class FrequencyDeduplicationService extends AbstractDeduplicationService {
    public FrequencyDeduplicationService(){
        deduplicationType = DeduplicationType.FREQUENCY.getCode();
    }

    public static final String LIMIT_TAG = "SP_";

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 业务规则去重 构建key
     * key：receiver + templateId + sendChannel
     * 用户在时间内只能收到某个渠道 N 次
     *
     * @param taskInfo
     * @param receiver
     * @return
     */
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return receiver + StrUtil.C_UNDERLINE
                + taskInfo.getMessageTemplateId() + StrUtil.C_UNDERLINE
                + taskInfo.getSendChannel();
    }

    /**
     * 普通计数
     * @param taskInfo
     * @param param
     * @return
     */
    @Override
    public Set<String> limitFilter(TaskInfo taskInfo, DeduplicationParam param) {
        HashSet<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());
        // 获取redis记录
        HashMap<String, String> readyPutRedisReceiver = new HashMap<>(taskInfo.getReceiver().size());
        // 获取当前模版所有去重key
        ArrayList<String> res = new ArrayList<>(taskInfo.getReceiver().size());
        for(String receiver : taskInfo.getReceiver()){
            String key = deduplicationSingleKey(taskInfo, receiver);
            res.add(key);
        }
        // redis数据隔离
        List<String> keys = res.stream().map(key -> LIMIT_TAG + key).collect(Collectors.toList());
        Map<String, String> inRedisValue = redisUtils.mGet(keys);

        for (String receiver : taskInfo.getReceiver()){
            String key = LIMIT_TAG + deduplicationSingleKey(taskInfo, receiver);
            String value = inRedisValue.get(key);
            // 符合条件的接收者
            if(Objects.nonNull(value) && Integer.parseInt(value) >= param.getCountNum()){
                filterReceiver.add(receiver);
            } else {
                readyPutRedisReceiver.put(receiver, key);
            }
        }
        // 不符合条件的接收者，需要更新redis（无记录添加，有记录则累加次数）
        putInRedis(readyPutRedisReceiver, inRedisValue, param.getDeduplicationTime());

        return filterReceiver;
    }

    /**
     * 存入redis
     * @param readyPutRedisReceiver
     * @param inRedisValue
     * @param deduplicationTime
     */
    private void putInRedis(HashMap<String, String> readyPutRedisReceiver, Map<String, String> inRedisValue, Long deduplicationTime) {
        HashMap<String, String> keyValues = new HashMap<>(readyPutRedisReceiver.size());
        for (Map.Entry<String, String> entry : readyPutRedisReceiver.entrySet()){
            String key = entry.getValue();
            if(Objects.nonNull(inRedisValue.get(key))){
                // 为了在value+1的时候过期时间不变
                Long expireTime = redisUtils.getExpireTime(key);
                redisUtils.lPush(key, String.valueOf(Integer.valueOf(inRedisValue.get(key)) + 1), expireTime);
            } else {
                keyValues.put(key, CommonConstant.ONE);
            }
        }
        // 不存在 key 就创建，设置过期时间
        if(CollUtil.isNotEmpty(keyValues)){
            redisUtils.pipelineSetEx(keyValues, deduplicationTime);
        }
    }
}
