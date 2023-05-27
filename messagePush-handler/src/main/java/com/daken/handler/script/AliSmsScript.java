package com.daken.handler.script;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.*;
import com.daken.handler.param.sms.SmsParam;
import com.daken.message.common.constant.CommonConstant;
import com.daken.message.common.dto.account.AliSmsAccount;
import com.daken.message.common.enums.SmsStatus;
import com.daken.message.support.domain.SmsRecord;
import com.daken.message.support.utils.AccountUtils;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 用阿里云服务发送短信的SDK
 */
@Slf4j
@Component
public class AliSmsScript implements SmsScript{

    @Autowired
    private AccountUtils accountUtils;

    @Override
    public List<SmsRecord> send(SmsParam smsParam) {
        try {
            AliSmsAccount account = accountUtils.getAccountById(smsParam.getSendAccountId(), AliSmsAccount.class);
            AsyncClient client = init(account);
            SendSmsRequest sendSmsRequest = assembleSendReq(smsParam, account);
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            SendSmsResponse res = response.get();
            String code = res.getBody().getCode();
            if(!CommonConstant.OK.equals(code)){
                log.error("错误信息:"  + res.getBody().getMessage() + "");
                client.close();
                return null;
            }
            client.close();
            return assembleSmsRecord(client, smsParam, res, account);
        } catch (Exception e){
            log.error("AlibabaSms send fail e:{}", e);
            return null;
        }
    }


    /**
     * 账号初始化
     * @param account
     * @return
     */
    private AsyncClient init(AliSmsAccount account){
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(account.getAccessKeyId())
                .accessKeySecret(account.getAccessKeySecret())
                .build());
        AsyncClient client = AsyncClient.builder()
                .region(account.getRegion())
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(account.endpoint)
                        .setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();
        return client;
    }

    /**
     * 组装参数
     * @param smsParam
     * @param account
     * @return
     */
    private SendSmsRequest assembleSendReq(SmsParam smsParam, AliSmsAccount account){
        StringBuilder phoneNumbers = new StringBuilder();
        for (String phoneNum : smsParam.getPhones()){
            phoneNumbers.append(phoneNum).append(CommonConstant.COMMA);
        }
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phoneNumbers.toString())
                .signName(account.getSignName())
                .build();
        return sendSmsRequest;
    }

    /**
     * 调用回执接口，组装 smsRecord 参数
     * @param client
     * @param smsParam
     * @param response
     * @param account
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private List<SmsRecord> assembleSmsRecord(AsyncClient client, SmsParam smsParam, SendSmsResponse response, AliSmsAccount account) throws ExecutionException, InterruptedException {
        if(Objects.isNull(response)){
            return null;
        }
        ArrayList<SmsRecord> smsRecordList = new ArrayList<>();
        String bizId = response.getBody().getBizId();
        for (String phoneNum : smsParam.getPhones()){
            QuerySendDetailsRequest queryReq = QuerySendDetailsRequest.builder().phoneNumber(phoneNum).bizId(bizId).sendDate(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN))
                    .currentPage(1L).pageSize(10L).build();
            CompletableFuture<QuerySendDetailsResponse> querySendDetailsResponseCompletableFuture = client.querySendDetails(queryReq);
            QuerySendDetailsResponse queryResp = querySendDetailsResponseCompletableFuture.get();
            QuerySendDetailsResponseBody.SmsSendDetailDTOs smsSendDetailDTOs = queryResp.getBody().getSmsSendDetailDTOs();
            for (QuerySendDetailsResponseBody.SmsSendDetailDTO dto : smsSendDetailDTOs.getSmsSendDetailDTO()){
                Integer status;
                if(CommonConstant.ONE_.equals(dto.getSendStatus())){
                    status = SmsStatus.RECEIVE_SUCCESS.getCode();
                } else if (CommonConstant.TWO.equals(dto.getSendStatus())) {
                    status = SmsStatus.SEND_FAIL.getCode();
                } else {
                    status = SmsStatus.SEND_SUCCESS.getCode();
                }
                SmsRecord smsRecord = SmsRecord.builder()
                        .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                        .messageTemplateId(smsParam.getMessageTemplateId())
                        .phone(Long.valueOf(phoneNum))
                        .supplierId(account.getSupplierId())
                        .supplierName(account.getSupplierName())
                        .msgContent(dto.getContent())
                        .status(status)
                        .created(Math.toIntExact(DateUtil.currentSeconds()))
                        .updated(Math.toIntExact(DateUtil.currentSeconds()))
                        .build();
                smsRecordList.add(smsRecord);
            }
        }
        return smsRecordList;
    }

}
