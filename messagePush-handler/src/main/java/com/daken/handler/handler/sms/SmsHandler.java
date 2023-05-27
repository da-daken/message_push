package com.daken.handler.handler.sms;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.daken.handler.handler.BaseHandler;
import com.daken.handler.param.sms.MessageTypeSmsConfig;
import com.daken.handler.param.sms.SmsParam;
import com.daken.handler.script.SmsScript;
import com.daken.message.common.constant.CommonConstant;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.dto.account.SmsAccount;
import com.daken.message.common.dto.model.SmsContentModel;
import com.daken.message.common.enums.ChannelType;
import com.daken.message.support.domain.MessageTemplate;
import com.daken.message.support.domain.SmsRecord;
import com.daken.message.support.service.ConfigService;
import com.daken.message.support.service.SmsRecordService;
import com.daken.message.support.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class SmsHandler extends BaseHandler {
    public SmsHandler(){
        channelCode = ChannelType.SMS.getCode();
    }

    public static final Integer AUTO_FLOW_RULE = 0;
    public static final String FLOW_KEY = "msgTypeSmsConfig";
    public static final String FLOW_KEY_PREFIX = "message_type_";
    @Resource
    private SmsRecordService smsRecordService;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConfigService config;

    @Override
    public boolean handler(TaskInfo taskInfo) {
        SmsContentModel smsContentModel = (SmsContentModel) taskInfo.getContentModel();
        SmsParam smsParam = SmsParam.builder()
                .phones(taskInfo.getReceiver())
                .messageTemplateId(taskInfo.getMessageTemplateId())
                .content(smsContentModel.getContent())
                .build();
        // 1. 负载均衡
        // 2. 发送短信
        MessageTypeSmsConfig[] messageTypeSmsConfigs = loadBalance(getMessageTypeSmsConfig(taskInfo));
        for (MessageTypeSmsConfig config : messageTypeSmsConfigs){
            smsParam.setScriptName(config.getScriptName());
            smsParam.setSendAccountId(config.getSendAccount());
            List<SmsRecord> recordList = applicationContext.getBean(config.getScriptName(), SmsScript.class).send(smsParam);
            if(CollUtil.isNotEmpty(recordList)){
                smsRecordService.saveBatch(recordList);
                return true;
            }
        }
        return false;
    }

    /**
     * 简单的负载均衡
     * 根据配置的权重优先走某个账号，并取出一个备份的
     *
     * @param messageTypeSmsConfigs
     * @return
     */
    private MessageTypeSmsConfig[] loadBalance(List<MessageTypeSmsConfig> messageTypeSmsConfigs){
        int total = 0;
        for (MessageTypeSmsConfig messageTypeSmsConfig : messageTypeSmsConfigs){
            total += messageTypeSmsConfig.getWeights();
        }
        // 生成一个随机数[1，total]，看落到哪个区间
        Random random = new Random();
        int index = random.nextInt(total) + 1;

        MessageTypeSmsConfig supplier = null;
        MessageTypeSmsConfig supplierBack = null;
        for (int i = 0; i < messageTypeSmsConfigs.size(); i++){
            if(index <= messageTypeSmsConfigs.get(i).getWeights()){
                supplier = messageTypeSmsConfigs.get(i);

                // 取下一个备份
                int j = (i + 1) % messageTypeSmsConfigs.size();
                if(i == j){
                    return new MessageTypeSmsConfig[]{supplier};
                }
                supplierBack = messageTypeSmsConfigs.get(j);
                return new MessageTypeSmsConfig[]{supplier, supplierBack};
            }
            index -= messageTypeSmsConfigs.get(i).getWeights();
        }
        return null;
    }

    /**
     * 如模板指定具体的明确账号，则优先发其账号，否则走到流量配置
     * <p>
     * 流量配置每种类型都会有其下发渠道账号的配置(流量占比也会配置里面)
     * <p>
     * 样例：
     * key：msgTypeSmsConfig
     * value：[{"message_type_10":[{"weights":80,"scriptName":"TencentSmsScript"},{"weights":20,"scriptName":"AliSmsScript"}]},{"message_type_20":[{"weights":20,"scriptName":"AliSmsScript"}]},{"message_type_30":[{"weights":20,"scriptName":"TencentSmsScript"}]},{"message_type_40":[{"weights":20,"scriptName":"TencentSmsScript"}]}]
     * 通知类短信有两个发送渠道 TencentSmsScript 占80%流量，AliSmsScript占20%流量
     * 营销类短信只有一个发送渠道 AliSmsScript
     * 验证码短信只有一个发送渠道 TencentSmsScript
     *
     * @param taskInfo
     * @return
     */
    private List<MessageTypeSmsConfig> getMessageTypeSmsConfig(TaskInfo taskInfo){
        /**
         * 如果模版指定了账号，则优先使用指定账号
         */
        if (!taskInfo.getSendAccount().equals(AUTO_FLOW_RULE)){
            SmsAccount account = accountUtils.getAccountById(taskInfo.getSendAccount(), SmsAccount.class);
            MessageTypeSmsConfig messageTypeSmsConfig = MessageTypeSmsConfig.builder()
                    .sendAccount(taskInfo.getSendAccount())
                    .scriptName(account.getScriptName())
                    .weights(100)
                    .build();
            return Arrays.asList(messageTypeSmsConfig);
        }
        /**
         * 读取流量配置
         */
        String property = config.getProperty(FLOW_KEY, CommonConstant.EMPTY_VALUE_JSON_ARRAY);
        JSONArray jsonArray = JSON.parseArray(property);
        for (int i = 0; i < jsonArray.size(); i++){
            JSONArray array = jsonArray.getJSONObject(i).getJSONArray(FLOW_KEY_PREFIX + taskInfo.getMsgType());
            if(CollUtil.isNotEmpty(array)){
                return JSON.parseArray(JSON.toJSONString(array), MessageTypeSmsConfig.class);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }


}
