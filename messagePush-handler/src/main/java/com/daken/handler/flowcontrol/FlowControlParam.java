package com.daken.handler.flowcontrol;

import com.daken.handler.flowcontrol.enums.RateLimitStrategy;
import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 限流参数
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowControlParam {
    /**
     * 限流器（每个渠道自己初始化）
     */
    protected RateLimiter rateLimiter;

    /**
     * 限流器初始大小
     */
    protected Double rateInitValue;

    /**
     * 限流策略
     */
    protected RateLimitStrategy strategy;
}
