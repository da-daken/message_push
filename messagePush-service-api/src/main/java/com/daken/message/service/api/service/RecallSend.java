package com.daken.message.service.api.service;

import com.daken.message.service.api.domain.SendRequest;
import com.daken.message.service.api.domain.SendResponse;

/**
 * @author daken
 *
 * 撤回接口
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
