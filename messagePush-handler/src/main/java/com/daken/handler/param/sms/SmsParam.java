package com.daken.handler.param.sms;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author daken
 *
 * 发送短信参数
 */
@Data
@Builder
public class SmsParam {

    /**
     * 业务ID
     */
    private Long messageTemplateId;
    /**
     * 接收者手机号
     */
    private Set<String> phones;
    /**
     * 发送短信的账号
     * 如果存在账号id，用账号登录
     */
    private Integer sendAccountId;
    /**
     * 发送渠道（服务商）
     */
    private String scriptName;
    /**
     * 发送文案
     */
    private String content;
}
