package com.daken.handler.param.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对于每种消息类型的短信配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageTypeSmsConfig {
    /**
     * 权重(决定流量占比)
     */
    private Integer weights;
    /**
     * 短信模版若指定了账号，则该字段没有值
     */
    private Integer sendAccount;
    /**
     * 短信的脚本名称
     */
    private String scriptName;
}
