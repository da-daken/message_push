package com.daken.message.service.api.impl.action;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daken.message.common.constant.CommonConstant;
import com.daken.message.common.domain.TaskInfo;
import com.daken.message.common.dto.model.ContentModel;
import com.daken.message.common.enums.ChannelType;
import com.daken.message.common.enums.RespStatusEnum;
import com.daken.message.common.vo.BasicResultVO;
import com.daken.message.service.api.domain.MessageParam;
import com.daken.message.service.api.enums.BusinessCode;
import com.daken.message.service.api.impl.domain.SendTaskModel;
import com.daken.message.support.domain.MessageTemplate;
import com.daken.message.support.pipeline.BusinessProcess;
import com.daken.message.support.pipeline.ProcessContext;
import com.daken.message.support.service.MessageTemplateService;
import com.daken.message.support.utils.ContentHolderUtil;
import com.daken.message.support.utils.TaskInfoUtils;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author daken
 *
 * 组装参数
 */
@Slf4j
@Service
public class AssembleAction implements BusinessProcess {
    private static final String LINK_NAME = "url";

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Override
    public void process(ProcessContext context) {
        SendTaskModel sendTaskModel = (SendTaskModel) context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();

        try {
            // 1. 根据模版ID查询是否存在
            MessageTemplate messageTemplate = messageTemplateService.getById(messageTemplateId);
            if (Objects.isNull(messageTemplate) || CommonConstant.TRUE.equals(messageTemplate.getIsDeleted())) {
                context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TEMPLATE_NOT_FOUND));
                log.error("组装参数失败！检查模版ID是否存在");
                return;
            }
            // 2. 组装参数
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) {
                List<TaskInfo> taskInfos = assembleTaskInfo(sendTaskModel, messageTemplate);
                sendTaskModel.setTaskInfo(taskInfos);
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) {
                sendTaskModel.setMessageTemplate(messageTemplate);
            }
        } catch (Exception e){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("组装参数失败！templateId:{}, e:{}", messageTemplateId, Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 组装参数 TaskInfo
     *
     * @param sendTaskModel
     * @param messageTemplate
     * @return
     */
    private List<TaskInfo> assembleTaskInfo(SendTaskModel sendTaskModel, MessageTemplate messageTemplate) {
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();
        List<TaskInfo> taskInfos = new ArrayList<>();
        for(MessageParam messageParam : messageParamList){
            TaskInfo taskInfo = TaskInfo.builder()
                    .messageTemplateId(messageTemplate.getId())
                    .businessId(TaskInfoUtils.generateBusinessId(messageTemplate.getId(), messageTemplate.getTemplateType()))
                    .receiver(new HashSet<>(Arrays.asList(messageParam.getReceiver().split(StrUtil.COMMA))))
                    .idType(messageTemplate.getIdType())
                    .msgType(messageTemplate.getMsgType())
                    .sendAccount(messageTemplate.getSendAccount())
                    .templateType(messageTemplate.getTemplateType())
                    .sendChannel(messageTemplate.getSendChannel())
                    .contentModel(getContentModelValue(messageTemplate, messageParam))
                    .build();
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }

    /**
     * 获取 contentModel，替换模板msgContent中占位符信息
     */
    private static ContentModel getContentModelValue(MessageTemplate messageTemplate, MessageParam messageParam) {

        // 得到真正的ContentModel 类型
        Integer sendChannel = messageTemplate.getSendChannel();
        Class<? extends ContentModel> contentModelClass = ChannelType.getChanelModelClassByCode(sendChannel);

        // 得到模板的 msgContent 和 入参
        Map<String, String> variables = messageParam.getVariables();
        JSONObject jsonObject = JSON.parseObject(messageTemplate.getMsgContent());


        // 通过反射 组装出 contentModel
        Field[] fields = ReflectUtil.getFields(contentModelClass);
        ContentModel contentModel = ReflectUtil.newInstance(contentModelClass);
        for (Field field : fields) {
            String originValue = jsonObject.getString(field.getName());
            if (StrUtil.isNotBlank(originValue)) {
                String resultValue = ContentHolderUtil.replacePlaceHolder(originValue, variables);
                Object resultObj = JSONUtil.isJsonObj(resultValue) ? JSONUtil.toBean(resultValue, field.getType()) : resultValue;
                ReflectUtil.setFieldValue(contentModel, field, resultObj);
            }
        }

        // 如果 url 字段存在，则在url拼接对应的埋点参数
        String url = (String) ReflectUtil.getFieldValue(contentModel, LINK_NAME);
        if (StrUtil.isNotBlank(url)) {
            String resultUrl = TaskInfoUtils.generateUrl(url, messageTemplate.getId(), messageTemplate.getTemplateType());
            ReflectUtil.setFieldValue(contentModel, LINK_NAME, resultUrl);
        }
        return contentModel;
    }
}
