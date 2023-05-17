package com.daken.message.support.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daken.message.support.domain.MessageTemplate;
import com.daken.message.support.mapper.MessageTemplateMapper;
import com.daken.message.support.service.MessageTemplateService;
import org.springframework.stereotype.Service;

/**
* @author daken
* @description 针对表【message_template(消息模板信息)】的数据库操作Service实现
* @createDate 2023-05-17 17:20:12
*/
@Service
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate>
    implements MessageTemplateService {

}




