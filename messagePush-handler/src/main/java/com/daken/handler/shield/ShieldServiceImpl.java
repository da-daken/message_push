package com.daken.handler.shield;

import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.ShieldType;
import com.daken.message.support.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class ShieldServiceImpl implements ShieldService {
    private static final String NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY = "night_shield_send";

    public static final long SECONDS_OF_A_DAY = 86400L;

    @Autowired
    private RedisUtils redisUtils;
    @Override
    public void shield(TaskInfo taskInfo) {
        if(ShieldType.NIGHT_NO_SHIELD.getCode().equals(taskInfo.getShieldType())){
            return ;
        }
        /**
         * ex:当消息下发到平台时，已经是凌晨1点，业务希望该消息在次日早上9点推送
         * 配合xxl-job使用
         */
        if(isNight()){
            if(ShieldType.NIGHT_SHIELD.getCode().equals(taskInfo.getShieldType())){

            }
            if(ShieldType.NIGHT_SHIELD_BUT_NEXT_DAY_SEND.getCode().equals(taskInfo.getShieldType())){

            }
            // 需要屏蔽就把接收者设为空
            taskInfo.setReceiver(new HashSet<>());
        }
    }

    /**
     * 判断是否是凌晨
     * @return
     */
    private boolean isNight() {
        return LocalDateTime.now().getHour() < 8;
    }
}
