package com.daken.handler.flowcontrol.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daken.handler.flowcontrol.FlowControlParam;
import com.daken.handler.flowcontrol.FlowControlService;
import com.daken.handler.flowcontrol.enums.RateLimitStrategy;
import com.daken.message.common.constant.CommonConstant;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.ChannelType;
import com.daken.message.support.service.ConfigService;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlowControlServiceImpl implements FlowControlService {
    private static final String FLOW_CONTROL_KEY = "flowControlRule";
    private static final String FLOW_CONTROL_PREFIX = "flow_control_";

    @Autowired
    private ConfigService configService;

    @Override
    public void flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        // 对比初始值和apollo中的限流值，以配置中心为准
        Double rateInitValue = flowControlParam.getRateInitValue();
        double costTime = 0;
        Double rateLimitConfig = getRateLimitConfig(taskInfo.getSendChannel());
        if(rateLimitConfig != null && !rateLimitConfig.equals(rateInitValue)){
            rateLimiter = RateLimiter.create(rateLimitConfig);
            flowControlParam.setRateInitValue(rateLimitConfig);
            flowControlParam.setRateLimiter(rateLimiter);
        }
        if(RateLimitStrategy.REQUEST_RATE_LIMIT.equals(flowControlParam.getStrategy())){
            costTime = rateLimiter.acquire(1);
        }
        if(RateLimitStrategy.SEND_USER_NUM_RATE_LIMIT.equals(flowControlParam.getStrategy())){
            costTime = rateLimiter.acquire(taskInfo.getReceiver().size());
        }
        if(costTime > 0){
            log.info("consumer {} flowControl time {}", taskInfo.getSendChannel(), costTime);
        }
    }

    /**
     * 从apollo中获取对应渠道的限流值
     *
     * apollo配置样例 ： key: flowControl  value : {"flow_control_40":1}
     * @param channelCode
     * @return
     */
    private Double getRateLimitConfig(Integer channelCode) {
        String flowControlConfig = configService.getProperty(FLOW_CONTROL_KEY, CommonConstant.EMPTY_VALUE_JSON_ARRAY);
        JSONObject jsonObject = JSON.parseObject(flowControlConfig);
        Double config = jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode);
        return config;
    }
}
