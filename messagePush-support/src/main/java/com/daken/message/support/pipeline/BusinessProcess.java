package com.daken.message.support.pipeline;

/**
 * 业务执行器
 */
public interface BusinessProcess {

    void process(ProcessContext context);
}
