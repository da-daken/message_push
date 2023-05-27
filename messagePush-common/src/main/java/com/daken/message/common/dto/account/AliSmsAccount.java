package com.daken.message.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliSmsAccount extends SmsAccount{
    /**
     * 账号相关
     */
    private String accessKeyId;
    private String accessKeySecret;
    private String region;
    private String signName;
    public final String endpoint = "dysmsapi.aliyuncs.com";
}
