package com.daken.stream.sink;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.daken.message.common.domain.AnchorInfo;
import com.daken.message.common.domain.FlinkAnchorInfo;
import com.daken.stream.utils.LettuceRedisUtils;
import io.lettuce.core.RedisFuture;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * flink 的 sink
 * 将消息写进 redis/hive
 */
public class MySink implements SinkFunction<AnchorInfo> {
    @Override
    public void invoke(AnchorInfo anchorInfo, Context context) throws Exception {
        /**
         * 实时数据写入redis
         */
        realTimeData(anchorInfo);
        /**
         * 离线数据写入hive
         */
        offlineDate(anchorInfo);
    }

    /**
     * 实时数据写入redis
     * 1.用户维度(查看用户当天收到消息的链路详情)，数量级大，只保留当天
     * 2.消息模板维度(查看消息模板整体下发情况)，数量级小，保留30天
     *
     * @param anchorInfo
     */
    private void offlineDate(AnchorInfo anchorInfo) {
        LettuceRedisUtils.pipeline(redisAsyncCommands -> {
            List<RedisFuture<?>> redisFutures = new ArrayList<>();
            /**
             * 1.构建userId维度的链路信息 数据结构list:{key,list}
             * key:userId,listValue:[{timestamp,state,businessId},{timestamp,state,businessId}]
             */
            FlinkAnchorInfo info = FlinkAnchorInfo.builder().state(anchorInfo.getState()).timestamp(anchorInfo.getLogTimestamp()).businessId(anchorInfo.getBusinessId()).build();
            for (String id : anchorInfo.getIds()) {
                redisFutures.add(redisAsyncCommands.lpush(id.getBytes(), JSON.toJSONString(info).getBytes()));
                redisFutures.add(redisAsyncCommands.expire(id.getBytes(), (DateUtil.endOfDay(new Date()).getTime() - DateUtil.current()) / 1000));
            }

            /**
             * 2.构建消息模板维度的链路信息 数据结构hash:{key,hash}
             * key:businessId,hashValue:{state,stateCount}
             */
            redisFutures.add(redisAsyncCommands.hincrby(String.valueOf(anchorInfo.getBusinessId()).getBytes(),
                    String.valueOf(anchorInfo.getState()).getBytes(), anchorInfo.getIds().size()));
            redisFutures.add(redisAsyncCommands.expire(String.valueOf(anchorInfo.getBusinessId()).getBytes(),
                    ((DateUtil.offsetDay(new Date(), 30).getTime()) / 1000) - DateUtil.currentSeconds()));

            return redisFutures;
        });
    }

    /**
     * todo 离线数据写入hive
     * @param anchorInfo
     */
    private void realTimeData(AnchorInfo anchorInfo) {

    }
}
