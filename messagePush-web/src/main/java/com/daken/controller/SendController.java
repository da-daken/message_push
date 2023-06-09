package com.daken.controller;

import com.daken.message.common.vo.BasicResultVO;
import com.daken.message.service.api.domain.SendRequest;
import com.daken.message.service.api.domain.SendResponse;
import com.daken.message.service.api.service.SendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
@Api(tags = {"发送消息"})
public class SendController {

    @Autowired
    private SendService sendService;

    @ApiOperation(value = "发送接口", notes = "多渠道发送消息，目前可支持邮件和短信")
    @PostMapping
    public BasicResultVO<String> send(@RequestBody SendRequest sendRequest){
        SendResponse sendResponse = sendService.send(sendRequest);
        return new BasicResultVO<>(sendResponse.getCode(), sendResponse.getMsg());
    }

}
