package com.daken.message.support.pipeline;

import lombok.Data;

import java.util.List;

/**
 * @author daken
 *
 * 业务执行模版（一条责任链）
 */
@Data
public class ProcessTemplate {
    private List<BusinessProcess> processList;
}
