package com.daken.handler.flowcontrol.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * 限流策略
 */
@AllArgsConstructor
@ToString
public enum RateLimitStrategy {
    /**
     * 根据真实请求数量限流（QPS）
     */
    REQUEST_RATE_LIMIT(10, "根据真实请求数量限流"),

    /**
     * 根据发送用户数限流（人数限流）
     */
    SEND_USER_NUM_RATE_LIMIT(20, "根据发送用户数限流")
    ;

    private final Integer code;
    private final String msg;
}
