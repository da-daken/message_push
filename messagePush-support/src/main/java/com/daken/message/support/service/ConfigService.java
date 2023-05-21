package com.daken.message.support.service;

/**
 * @author daken
 *
 * 读取配置服务
 */
public interface ConfigService {
    /**
     * 1. 启动apollo，优先读取远程配置
     * 2. 当没有启动远程配置，读取本地yml文件
     * @param key
     * @param defaultValue
     * @return
     */
    String getProperty(String key, String defaultValue);
}
