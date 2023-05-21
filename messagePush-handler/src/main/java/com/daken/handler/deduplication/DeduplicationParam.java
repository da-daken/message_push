package com.daken.handler.deduplication;

import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.enums.AnchorState;
import lombok.Builder;
import lombok.Data;

/**
 * @author daken
 *
 * 去重服务的参数
 */
@Data
@Builder
public class DeduplicationParam {

    private TaskInfo taskInfo;
    /**
     * 去重时间
     * 单位：秒
     */
    private Long deduplicationTime;
    /**
     * 需要去重的次数临界值
     */
    private Integer countNum;
    /**
     * 标识属于哪种去重（数据埋点）
     */
    private AnchorState anchorState;
}
