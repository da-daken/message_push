package com.daken.handler.script;

import com.daken.handler.param.sms.SmsParam;
import com.daken.message.support.domain.SmsRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用阿里云服务发送短信的SDK
 */
@Slf4j
@Component
public class AliSmsScript implements SmsScript{
    public static final Integer PHONE_NUM = 11;

    @Override
    public List<SmsRecord> send(SmsParam smsParam) {
        return null;
    }

    @Override
    public List<SmsRecord> pull(Integer id) {
        return null;
    }
}
