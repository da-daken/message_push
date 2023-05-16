package com.daken.message.service.api.impl.service;

import com.daken.message.service.api.domain.BatchSendRequest;
import com.daken.message.service.api.domain.SendRequest;
import com.daken.message.service.api.domain.SendResponse;
import com.daken.message.service.api.service.SendService;
import org.springframework.stereotype.Service;

@Service
public class SendServiceImpl implements SendService {
    @Override
    public SendResponse send(SendRequest sendRequest) {
        return null;
    }

    @Override
    public SendResponse batchSend(BatchSendRequest batchSendRequest) {
        return null;
    }
}
