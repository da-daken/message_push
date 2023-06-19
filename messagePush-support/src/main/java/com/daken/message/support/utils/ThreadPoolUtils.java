package com.daken.message.support.utils;

import com.daken.message.support.config.ThreadPoolExecutorShutdownDefinition;
import com.dtp.common.ex.DtpException;
import com.dtp.core.DtpRegistry;
import com.dtp.core.thread.DtpExecutor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * 线程池工具类
 *
 * @author daken
 */
public class ThreadPoolUtils {
    @Resource
    private ThreadPoolExecutorShutdownDefinition shutdownDefinition;

    public static final String SOURCE_NAME = "daken";

    /**
     * 1. 将当前线程池加入到动态线程池中
     * 2. 注册线程池给spring管理，进行优雅关闭
     */
    public void register(DtpExecutor dtpExecutor){
        DtpRegistry.register(dtpExecutor, SOURCE_NAME);
        shutdownDefinition.registryExecutor(dtpExecutor);
    }

}
