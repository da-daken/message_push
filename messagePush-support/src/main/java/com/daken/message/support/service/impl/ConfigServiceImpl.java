package com.daken.message.support.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.ctrip.framework.apollo.Config;
import com.daken.message.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ConfigServiceImpl implements ConfigService {
    public static final String PROPERTIES_PATH = "local.properties";
    private Props props = new Props(PROPERTIES_PATH, StandardCharsets.UTF_8);

    /**
     * apollo 配置
     */
    @Value("${apollo.bootstrap.enabled}")
    private Boolean enableApollo;
    @Value("${apollo.bootstrap.namespaces}")
    private String namespaces;

    @Override
    public String getProperty(String key, String defaultValue) {
        if(enableApollo){
            Config config = com.ctrip.framework.apollo.ConfigService.getConfig(namespaces.split(StrUtil.COMMA)[0]);
            return config.getProperty(key, defaultValue);
        } else {
            return props.getProperty(key, defaultValue);
        }
    }
}
