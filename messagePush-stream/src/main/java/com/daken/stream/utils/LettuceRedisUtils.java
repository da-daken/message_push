package com.daken.stream.utils;

import com.daken.stream.callback.RedisPipelineCallBack;
import com.daken.stream.constants.FlinkConstants;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 在无spring环境下使用redis，基于Lettuce封装
 *
 * @author daken
 */
public class LettuceRedisUtils {
    /**
     * 初始化 redis client
     */
    private static RedisClient redisClient;

    static {
        RedisURI redisUri = RedisURI.Builder.redis(FlinkConstants.REDIS_IP)
                .withPort(Integer.valueOf(FlinkConstants.REDIS_PORT))
                .withPassword(FlinkConstants.REDIS_PASSWORD.toCharArray())
                .build();
        redisClient = RedisClient.create(redisUri);
    }

    /**
     * 封装 pipeline
     *
     * @param pipelineCallBack 用来执行命令的
     */
    public static void pipeline(RedisPipelineCallBack pipelineCallBack){
        StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(new ByteArrayCodec());
        RedisAsyncCommands<byte[], byte[]> commands = connect.async();

        List<RedisFuture<?>> futures = pipelineCallBack.invoke(commands);

        commands.flushCommands();
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS,
                futures.toArray(new RedisFuture[futures.size()]));
        connect.close();
    }
}
