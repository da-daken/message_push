package com.daken.message.service.api.service;

import com.daken.message.service.api.domain.SendRequest;
import com.daken.message.service.api.domain.SendResponse;

/**
 * @author daken
 *
 * 撤回接口 (只有一些渠道支持撤回操作,对于不能支持的渠道打个日志)
 */
public interface RecallSend {

    /**
     * 撤回消息根据模版ID
     *
     * @param sendRequest
     * @return
     */
    SendResponse recall(SendRequest sendRequest);
}
