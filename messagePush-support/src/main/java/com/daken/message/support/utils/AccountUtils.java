package com.daken.message.support.utils;

import com.alibaba.fastjson.JSON;
import com.daken.message.support.domain.ChannelAccount;
import com.daken.message.support.service.ChannelAccountService;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
public class AccountUtils {

    @Resource
    private ChannelAccountService accountService;

    public <T> T getAccountById(Integer sendAccountId, Class<T> clazz){
        try {
            ChannelAccount account = accountService.getById(Long.valueOf(sendAccountId));
            if(Objects.isNull(account)){
                throw new Exception("请正确检查渠道账号");
            }
            return JSON.parseObject(account.getAccountConfig(), clazz);
        } catch (Exception e){
            log.error("获取账号失败 e:{}", Throwables.getStackTraceAsString(e));
        }
        return null;
    }

}
