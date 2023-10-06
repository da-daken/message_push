package com.daken.message.common.enums;


import com.daken.message.common.dto.model.ContentModel;
import com.daken.message.common.dto.model.EmailContentModel;
import com.daken.message.common.dto.model.PushContentModel;
import com.daken.message.common.dto.model.SmsContentModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

/**
 * 发送渠道类型枚举
 *
 * @author daken
 */
@Getter
@ToString
@AllArgsConstructor
public enum ChannelType implements PowerfulEnum {



    /**
     * sms(短信)  -- 腾讯云、阿里云
     */
    SMS(30, "sms(短信)", SmsContentModel.class, "sms", "", null),
    /**
     * email(邮件) -- QQ、163邮箱
     */
    EMAIL(40, "email(邮件)", EmailContentModel.class, "email", "", null),
    /**
     * push(通知栏) --安卓,ios 使用个推实现
     */
    PUSH(20, "push(通知栏)", PushContentModel.class, "push", "ge_tui_access_token_", 3600 * 24L),
    ;

    /**
     * 编码值
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String description;

    /**
     * 内容模型Class
     */
    private final Class<? extends ContentModel> contentModelClass;

    /**
     * 英文标识
     */
    private final String codeEn;

    /**
     * accessToken prefix
     */
    private final String accessTokenPrefix;

    /**
     * accessToken expire
     * 单位秒
     */
    private final Long accessTokenExpire;


    /**
     * 通过code获取class
     *
     * @param code
     * @return
     */
    public static Class<? extends ContentModel> getChanelModelClassByCode(Integer code) {
        return Arrays.stream(values()).filter(channelType -> Objects.equals(code, channelType.getCode()))
                .map(ChannelType::getContentModelClass)
                .findFirst().orElse(null);
    }
}
