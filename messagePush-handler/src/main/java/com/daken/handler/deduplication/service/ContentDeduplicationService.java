package com.daken.handler.deduplication.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.DeduplicationType;
import com.daken.message.support.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author daken
 *
 * 内容去重
 */
@Service
public class ContentDeduplicationService extends AbstractDeduplicationService{
    public ContentDeduplicationService(){
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    public static final String LIMIT_TAG = "SW_";

    @Autowired
    private RedisUtils redisUtils;

    private DefaultRedisScript<Long> redisScript;

    @PostConstruct
    public void init(){
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }

    /**
     * 内容去重，构建key
     * key：md5（templateId + receiver + content）
     * 相同的内容相同的模版，短时间内发给同一个人
     *
     * @param taskInfo
     * @param receiver
     * @return
     */
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return DigestUtil.md5Hex(taskInfo.getMessageTemplateId() + receiver
                + JSON.toJSONString(taskInfo.getContentModel()));
    }

    /**
     * 滑动窗口去重，使用redis中zset数据结构，可以做到严格控制单位时间内的数量
     * 把 zset 的 score 当成时间戳窗口
     * @param taskInfo
     * @param param
     * @return
     */
    @Override
    public Set<String> limitFilter(TaskInfo taskInfo, DeduplicationParam param) {
        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());
        for(String receiver : taskInfo.getReceiver()){
            String key = LIMIT_TAG + deduplicationSingleKey(taskInfo, receiver);
            // 要保证 Zset 里的元素不被覆盖，所以用雪花算法生成
            String value = String.valueOf(IdUtil.getSnowflake().nextId());
            long nowTime = System.currentTimeMillis();
            if(redisUtils.execLimitLua(redisScript, Collections.singletonList(key), String.valueOf(param.getDeduplicationTime() * 1000),
                    String.valueOf(nowTime), String.valueOf(param.getCountNum()), value)){
                filterReceiver.add(receiver);
            }
        }
        return filterReceiver;
    }
}
