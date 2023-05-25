package com.daken.handler.script;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.daken.handler.param.sms.SmsParam;
import com.daken.message.common.dto.account.TencentSmsAccount;
import com.daken.message.common.enums.SmsStatus;
import com.daken.message.support.domain.ChannelAccount;
import com.daken.message.support.domain.SmsRecord;
import com.daken.message.support.service.ChannelAccountService;
import com.daken.message.support.utils.AccountUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TencentSmsScript implements SmsScript{
    public static final Integer PHONE_NUM = 11;

    @Autowired
    private AccountUtils accountUtils;

    @Override
    public List<SmsRecord> send(SmsParam smsParam) {
        try {
            TencentSmsAccount account = accountUtils.getAccountById(smsParam.getSendAccountId(), TencentSmsAccount.class);
            SmsClient client = init(account);
            SendSmsRequest request = assembleSendRq(smsParam, account);
            SendSmsResponse response = client.SendSms(request);
            return assembleSmsRecord(smsParam, response, account);
        } catch (Exception e) {
            log.error("TencentSms send fail e:{}", e);
            return null;
        }
    }

    @Override
    public List<SmsRecord> pull(Integer id) {
        return null;
    }

    /**
     * 初始化
     * @param account
     * @return
     */
    private SmsClient init(TencentSmsAccount account){
        Credential credential = new Credential(account.getSecretId(), account.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(account.getUrl());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        SmsClient smsClient = new SmsClient(credential, account.getRegion(), clientProfile);
        return smsClient;
    }

    /**
     *  组装短信参数
     * @param smsParam
     * @param account
     * @return
     */
    private SendSmsRequest assembleSendRq(SmsParam smsParam, TencentSmsAccount account){
        SendSmsRequest req = new SendSmsRequest();
        String[] phoneNumbers = smsParam.getPhones().toArray(new String[smsParam.getPhones().size() - 1]);
        req.setPhoneNumberSet(phoneNumbers);
        req.setSmsSdkAppId(account.getSmsSdkAppId());
        req.setSignName(account.getSignName());
        req.setTemplateId(account.getTemplateId());
        String[] templateParams = {smsParam.getContent()};
        req.setTemplateParamSet(templateParams);
        req.setSessionContext(IdUtil.fastSimpleUUID());
        return req;
    }

    /**
     * 组装记录在数据的记录
     * @param smsParam
     * @param response
     * @param account
     * @return
     */
    private List<SmsRecord> assembleSmsRecord(SmsParam smsParam, SendSmsResponse response, TencentSmsAccount account){
        if(Objects.isNull(response) || ArrayUtil.isEmpty(response.getSendStatusSet())){
            return null;
        }
        ArrayList<SmsRecord> smsRecordList = new ArrayList<>();
        for (SendStatus sendStatus : response.getSendStatusSet()){
            // 腾讯返回的电话号有前缀,可以直接翻转取后面的
            String phone = new StringBuilder(new StringBuilder(sendStatus.getPhoneNumber())
                    .reverse().substring(0, PHONE_NUM)).reverse().toString();
            SmsRecord smsRecord = SmsRecord.builder()
                    .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                    .messageTemplateId(smsParam.getMessageTemplateId())
                    .phone(Long.valueOf(phone))
                    .supplierId(account.getSupplierId())
                    .supplierName(account.getSupplierName())
                    .msgContent(smsParam.getContent())
                    .seriesId(sendStatus.getSerialNo())
                    .chargingNum(Math.toIntExact(sendStatus.getFee()))
                    .status(SmsStatus.SEND_SUCCESS.getCode())
                    .reportContent(sendStatus.getCode())
                    .created(Math.toIntExact(DateUtil.currentSeconds()))
                    .updated(Math.toIntExact(DateUtil.currentSeconds()))
                    .build();
            smsRecordList.add(smsRecord);
        }
        return smsRecordList;
    }

}
