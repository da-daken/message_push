package com.daken.message.common.constant;


/**
 * 发送渠道的URL常量，统一维护发送地址
 *
 * @author daken
 */
public class SendChanelUrlConstant {

    /**
     * 个推相关的url
     */
    public static final String GE_TUI_BASE_URL = "https://restapi.getui.com/v2/";
    public static final String GE_TUI_SINGLE_PUSH_PATH = "/push/single/cid";
    public static final String GE_TUI_BATCH_PUSH_CREATE_TASK_PATH = "/push/list/message";
    public static final String GE_TUI_BATCH_PUSH_PATH = "/push/list/cid";
    public static final String GE_TUI_AUTH = "/auth";

}
