package com.daken.message.common.enums;


import com.daken.message.common.dto.model.ContentModel;
import com.daken.message.common.dto.model.EmailContentModel;
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
     * sms(短信)  -- 腾讯云、云片
     */
    SMS(30, "sms(短信)", SmsContentModel.class, "sms"),
    /**
     * email(邮件) -- QQ、163邮箱
     */
    EMAIL(40, "email(邮件)", EmailContentModel.class, "email"),
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
