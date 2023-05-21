package com.daken.handler.deduplication.build;

import com.alibaba.fastjson.JSONObject;
import com.daken.handler.deduplication.DeduplicationParam;
import com.daken.message.common.enums.AnchorState;
import com.daken.message.common.enums.DeduplicationType;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ContentDeduplicationBuilder extends AbstractDeduplicationParamBuilder{
    public ContentDeduplicationBuilder(){
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }
    @Override
    public DeduplicationParam build(JSONObject jsonObject) {
        DeduplicationParam deduplicationParam = JSONObject.parseObject(jsonObject.getString(DEDUPLICATION_CONFIG_PRE + deduplicationType), DeduplicationParam.class);
        if(Objects.isNull(deduplicationParam)){
            return null;
        }
        deduplicationParam.setAnchorState(AnchorState.CONTENT_DEDUPLICATION);
        return deduplicationParam;
    }
}
