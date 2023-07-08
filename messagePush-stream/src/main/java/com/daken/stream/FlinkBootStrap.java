package com.daken.stream;

import com.daken.message.common.domain.AnchorInfo;
import com.daken.stream.constants.FlinkConstants;
import com.daken.stream.function.MyFlatMapFunction;
import com.daken.stream.sink.MySink;
import com.daken.stream.utils.MqUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * flink 启动类
 *
 * @author daken
 */
@Slf4j
public class FlinkBootStrap {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /**
         * 1. 获取kafka consumer
         */
        KafkaSource<String> kafkaConsumer = MqUtils.getKafkaConsumer(FlinkConstants.TOPIC_NAME, FlinkConstants.GROUP_ID, FlinkConstants.BROKER);
        DataStreamSource<String> kafkaSource = env.fromSource(kafkaConsumer, WatermarkStrategy.noWatermarks(), FlinkConstants.SOURCE_NAME);

        /**
         * 2. 数据转换处理
         */
        SingleOutputStreamOperator<AnchorInfo> dataStream = kafkaSource.flatMap(new MyFlatMapFunction()).name(FlinkConstants.FUNCTION_NAME);

        /**
         * 3. 加入sink
         * 将实时数据多维度写入Redis(已实现)，离线数据写入hive(未实现)
         */
        dataStream.addSink(new MySink()).name(FlinkConstants.SINK_NAME);
        env.execute(FlinkConstants.JOB_NAME);
    }
}