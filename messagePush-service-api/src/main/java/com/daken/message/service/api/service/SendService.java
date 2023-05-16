package com.daken.message.service.api.service;

import com.daken.message.service.api.domain.BatchSendRequest;
import com.daken.message.service.api.domain.SendRequest;
import com.daken.message.service.api.domain.SendResponse;

/**
 * @author daken
 *
 * 发送接口
 */
public interface SendService {
    /**
     * 发送接口（单条消息）
     *
     * @param sendRequest
     * @return
     */
    SendResponse send(SendRequest sendRequest);

    /**
     * 可以多种文案对应不同的接收者同时发送
     *
     * @param batchSendRequest
     * @return
     */
    SendResponse batchSend(BatchSendRequest batchSendRequest);
}
