package com.daken.message.support.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daken.message.support.mapper.SmsRecordMapper;
import com.daken.message.support.domain.SmsRecord;
import com.daken.message.support.service.SmsRecordService;
import org.springframework.stereotype.Service;

/**
* @author daken
* @description 针对表【sms_record(短信记录信息)】的数据库操作Service实现
* @createDate 2023-05-25 11:01:33
*/
@Service
public class SmsRecordServiceImpl extends ServiceImpl<SmsRecordMapper, SmsRecord>
    implements SmsRecordService {

}




