package com.daken.message.support.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daken
 * @date 2022/5/8
 * <p>
 * https://docs.getui.com/getui/server/rest_v2/token/
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class GeTuiTokenResultDTO {


    @JSONField(name = "msg")
    private String msg;
    @JSONField(name = "code")
    private Integer code;
    @JSONField(name = "data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JSONField(name = "expire_time")
        private String expireTime;
        @JSONField(name = "token")
        private String token;
    }
}
