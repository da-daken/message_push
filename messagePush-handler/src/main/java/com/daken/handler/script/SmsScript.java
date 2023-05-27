package com.daken.handler.script;

import com.daken.handler.param.sms.SmsParam;
import com.daken.message.support.domain.SmsRecord;

import java.util.List;

public interface SmsScript {
    /**
     * 发送短信
     * @param smsParam
     * @return 返回记录，保存在数据库
     */
    List<SmsRecord> send(SmsParam smsParam);

}
