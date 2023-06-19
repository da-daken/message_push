package com.daken.handler.handler.impl;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.daken.handler.flowcontrol.FlowControlParam;
import com.daken.handler.flowcontrol.enums.RateLimitStrategy;
import com.daken.handler.handler.BaseHandler;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.dto.model.EmailContentModel;
import com.daken.message.common.enums.ChannelType;
import com.daken.message.support.domain.MessageTemplate;
import com.daken.message.support.utils.AccountUtils;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.RateLimiter;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Objects;

@Component
@Slf4j
public class EmailHandler extends BaseHandler {
    public EmailHandler(){
        channelCode = ChannelType.EMAIL.getCode();
        // 按照请求限流，默认单机 3 qps （具体数值配置在apollo动态调整)
        Double rateInitValue = Double.valueOf(3);
        flowControlParam = FlowControlParam.builder().rateInitValue(rateInitValue)
                .strategy(RateLimitStrategy.REQUEST_RATE_LIMIT)
                .rateLimiter(RateLimiter.create(rateInitValue)).build();
    }

    @Value("${daken.mail.upload.path}")
    private String dataPath;


    @Autowired
    private AccountUtils accountUtils;
    @Override
    public boolean handler(TaskInfo taskInfo) {
        try {
            EmailContentModel emailContentModel = (EmailContentModel) taskInfo.getContentModel();
            MailAccount mailAccount = getAccountConfig(taskInfo.getSendAccount());
            File file = StrUtil.isNotEmpty(emailContentModel.getUrl()) ? getRemoteUrl2File(dataPath, emailContentModel.getUrl()) : null;
            if (Objects.isNull(file)) {
                MailUtil.send(mailAccount, taskInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true);
            } else {
                MailUtil.send(mailAccount, taskInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true, file);
            }
        } catch (Exception e){
            log.error("EmailHandler#handler fail!{},params:{}", Throwables.getStackTraceAsString(e), taskInfo);
            return false;
        }
        return true;
    }

    /**
     * 封装 mail 账号
     * @param sendAccount
     * @return
     */
    private MailAccount getAccountConfig(Integer sendAccount) {
        MailAccount mailAccount = accountUtils.getAccountById(sendAccount, MailAccount.class);
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            mailAccount.setAuth(mailAccount.isAuth()).setStarttlsEnable(mailAccount.isStarttlsEnable()).setSslEnable(mailAccount.isSslEnable()).setCustomProperty("mail.smtp.ssl.socketFactory", sf);
            mailAccount.setTimeout(25000).setConnectionTimeout(25000);
        } catch (Exception e) {
            log.error("EmailHandler#getAccount fail!{}", Throwables.getStackTraceAsString(e));
        }
        return mailAccount;
    }

    /**
     * 将附件链接转换成文件（先拉下来存在指定路径）
     * @param dataPath
     * @param remoteUrl
     * @return
     */
    private File getRemoteUrl2File(String dataPath, String remoteUrl) {
        try {
            URL url = new URL(remoteUrl);
            File file = new File(dataPath, url.getPath());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                IoUtil.copy(url.openStream(), new FileOutputStream(file));
            }
            return file;
        } catch (Exception e) {
            log.error("AustinFileUtils#getRemoteUrl2File fail:{},remoteUrl:{}", Throwables.getStackTraceAsString(e), remoteUrl);
        }
        return null;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
