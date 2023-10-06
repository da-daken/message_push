package com.daken.handler.param.getui;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 推送消息的param
 *
 * @author daken
 * https://docs.getui.com/getui/server/rest_v2/push/
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SendPushParam {

    /**
     * 请求唯一标识号，10-32位之间；如果request_id重复，会导致消息丢失
     */
    @JSONField(name = "request_id")
    private String requestId;
    /**
     * 推送条件设置
     */
    @JSONField(name = "settings")
    private SettingsVO settings;
    /**
     * 推送目标用户
     */
    @JSONField(name = "audience")
    private AudienceVO audience;
    /**
     * 个推推送消息参数(在线)
     */
    @JSONField(name = "push_message")
    private PushMessageVO pushMessage;

    /**
     * SettingsVO
     */
    @NoArgsConstructor
    @Data
    public static class SettingsVO {
        /**
         * ttl
         */
        @JSONField(name = "ttl")
        private Integer ttl;
    }

    /**
     * AudienceVO
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class AudienceVO {
        /**
         * cid
         */
        @JSONField(name = "cid")
        private Set<String> cid;
    }

    /**
     * PushMessageVO
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class PushMessageVO {
        /**
         * notification
         */
        @JSONField(name = "notification")
        private NotificationVO notification;

        /**
         * NotificationVO
         */
        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class NotificationVO {
            /**
             * title
             */
            @JSONField(name = "title")
            private String title;
            /**
             * body
             */
            @JSONField(name = "body")
            private String body;
            /**
             * clickType
             */
            @JSONField(name = "click_type")
            private String clickType;
            /**
             * url
             */
            @JSONField(name = "url")
            private String url;
        }
    }
}
