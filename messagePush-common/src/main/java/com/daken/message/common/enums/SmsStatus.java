package com.daken.message.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 短信状态信息
 *
 */
@Getter
@ToString
@AllArgsConstructor
public enum SmsStatus implements PowerfulEnum {

    /**
     * 调用渠道接口发送成功
     */
    SEND_SUCCESS(10, "调用渠道接口发送成功"),
    /**
     * 正在等待回执
     */
    RECEIVE_SUCCESS(20, "正在等待回执"),
    /**
     * 调用渠道接口发送失败
     */
    SEND_FAIL(40, "调用渠道接口发送失败");

    private final Integer code;
    private final String description;
}
