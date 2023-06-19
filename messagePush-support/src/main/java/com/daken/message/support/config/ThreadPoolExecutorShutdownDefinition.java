package com.daken.message.support.config;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 优雅关闭线程池(监听关闭事件)
 * spring的容器在关闭的时候会发布一个关闭事件，所以我们可以监听spring容器关闭事件，在接收到事件的时候执行优雅关闭
 * @author daken
 */
@Component
@Slf4j
public class ThreadPoolExecutorShutdownDefinition implements ApplicationListener<ContextClosedEvent> {
    private final List<ExecutorService> POOLS = new ArrayList<>(12);

    /**
     * 线程中的任务在接收到应用关闭信号量后最多等待多久就强制终止，给剩余任务预留到时间
     */
    private final long AWAIT_TERMINATION = 20;

    public void registryExecutor(ExecutorService executor){
        POOLS.add(executor);
    }

    /**
     * 参考{@link ExecutorConfigurationSupport#shutdown()}
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("容器关闭前优雅关闭线程池开始，当前要处理到线程池数量：{} -----", POOLS.size());
        if(CollectionUtils.isEmpty(POOLS)){
            return ;
        }
        for(ExecutorService pool : POOLS){
            pool.shutdown();
            try {
                if(!pool.awaitTermination(AWAIT_TERMINATION, TimeUnit.SECONDS)){
                    if(log.isWarnEnabled()){
                        log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                    }
                }
            } catch (InterruptedException e){
                if(log.isWarnEnabled()){
                    log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                }
                Thread.currentThread().interrupt();
            }

        }
    }
}
