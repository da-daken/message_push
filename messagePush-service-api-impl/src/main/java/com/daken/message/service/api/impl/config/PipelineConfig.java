package com.daken.message.service.api.impl.config;

import com.daken.message.service.api.enums.BusinessCode;
import com.daken.message.service.api.impl.action.AfterParamCheckAction;
import com.daken.message.service.api.impl.action.AssembleAction;
import com.daken.message.service.api.impl.action.PreParamCheckAction;
import com.daken.message.service.api.impl.action.SendMqAction;
import com.daken.message.support.pipeline.ProcessController;
import com.daken.message.support.pipeline.ProcessTemplate;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author daken
 *
 * 设置责任链
 */
@Configuration
public class PipelineConfig {
    @Autowired
    private PreParamCheckAction preParamCheckAction;
    @Autowired
    private AssembleAction assembleAction;
    @Autowired
    private AfterParamCheckAction afterParamCheckAction;
    @Autowired
    private SendMqAction sendMqAction;

    /**
     * 发送消息的责任链
     * 1. 前置参数检查
     * 2. 组装参数
     * 3. 后置参数检查
     * 4. 发送消息到 mq 中
     */
    @Bean("commonSendTemplate")
    public ProcessTemplate commonSendTemplate(){
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(preParamCheckAction, assembleAction, afterParamCheckAction, sendMqAction));
        return processTemplate;
    }

    /**
     * 撤回消息到责任链
     * 1. 组装参数
     * 2. 发送消息到 mq 中
     */
    @Bean("recallMessageTemplate")
    public ProcessTemplate recallMessageTemplate(){
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(assembleAction, sendMqAction));
        return processTemplate;
    }

    /**
     * 设置不同 code 对应的责任链
     */
    @Bean
    public ProcessController processController(){
        ProcessController processController = new ProcessController();
        HashMap<String, ProcessTemplate> templateConfig = new HashMap<>();
        templateConfig.put(BusinessCode.COMMON_SEND.getCode(), commonSendTemplate());
        templateConfig.put(BusinessCode.RECALL.getCode(), recallMessageTemplate());
        processController.setTemplateConfig(templateConfig);
        return processController;
    }
}
