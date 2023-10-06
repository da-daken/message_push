package com.daken.message.support.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.daken.message.common.constant.SendChanelUrlConstant;
import com.daken.message.common.enums.ChannelType;
import com.daken.message.common.enums.EnumUtil;
import com.daken.message.support.dto.GeTuiTokenResultDTO;
import com.daken.message.support.dto.QueryTokenParamDTO;
import com.google.common.base.Throwables;
import com.daken.message.common.dto.account.GeTuiAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 获取第三发token工具类
 *
 * @author daken
 */
@Slf4j
@Component
public class AccessTokenUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取 对应渠道的accessToken
     * 1，redis存在，则直接从redis取
     * 2，redis不存在，调用底层方法去获取accessToken，并加入到redis中
     *
     * @param sendChannel
     * @param accountId   账号Id（数据库的主键）
     * @param account     渠道的对应的账号详情
     * @param refresh     是否要强制刷新现有的缓存accessToken
     * @return
     * @see com.daken.message.common.enums.ChannelType
     */
    public String getAccessToken(Integer sendChannel, Integer accountId, Object account, Boolean refresh) {
        String resultToken = "";

        // expireTime跟渠道的accessToken失效有关（个推accessToken默认有效是1天）
        String accessTokenPrefix = EnumUtil.getEnumByCode(sendChannel, ChannelType.class).getAccessTokenPrefix();
        Long expireTime = EnumUtil.getEnumByCode(sendChannel, ChannelType.class).getAccessTokenExpire();

        try {
            resultToken = redisTemplate.opsForValue().get(accessTokenPrefix + accountId);
            if (StrUtil.isNotBlank(resultToken) && !refresh) {
                return resultToken;
            }
            resultToken = getGeTuiAccessToken(account);
            if (StrUtil.isNotBlank(resultToken)) {
                redisTemplate.opsForValue().set(accessTokenPrefix + accountId, resultToken, expireTime, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("AccessTokenUtils#getAccessToken fail,sendChannel:[{}],accountId:[{}],error mgs:{}", sendChannel, accountId, Throwables.getStackTraceAsString(e));
        }
        return resultToken;

    }

    /**
     * 获取个推的 access_token
     *
     * @param account 创建个推账号时的元信息
     * @return 个推的 access_token
     */
    private String getGeTuiAccessToken(Object account) {
        String accessToken = "";
        try {
            GeTuiAccount geTuiAccount = (GeTuiAccount) account;
            String url = SendChanelUrlConstant.GE_TUI_BASE_URL + geTuiAccount.getAppId() + SendChanelUrlConstant.GE_TUI_AUTH;
            String time = String.valueOf(System.currentTimeMillis());
            String digest = SecureUtil.sha256().digestHex(geTuiAccount.getAppKey() + time + geTuiAccount.getMasterSecret());
            QueryTokenParamDTO param = QueryTokenParamDTO.builder()
                    .timestamp(time)
                    .appKey(geTuiAccount.getAppKey())
                    .sign(digest).build();

            String body = HttpRequest.post(url).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .body(JSON.toJSONString(param))
                    .timeout(2000)
                    .execute().body();
            GeTuiTokenResultDTO geTuiTokenResultDTO = JSON.parseObject(body, GeTuiTokenResultDTO.class);
            if (geTuiTokenResultDTO.getCode().equals(0)) {
                accessToken = geTuiTokenResultDTO.getData().getToken();
            }
        } catch (Exception e) {
            log.error("AccessTokenUtils#getGeTuiAccessToken fail:{}", Throwables.getStackTraceAsString(e));
        }
        return accessToken;
    }
}
