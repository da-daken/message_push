package com.daken.message.support.mq;

/**
 * @author daken
 *
 * 提供一个接口进行多个mq复用
 */
public interface SendMqService {
    void send(String topic, String jsonValue, String tagId);

    void send(String topic, String jsonValue);
}
