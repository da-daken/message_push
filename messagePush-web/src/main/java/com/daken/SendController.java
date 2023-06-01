package com.daken;

import com.daken.message.common.vo.BasicResultVO;
import com.daken.message.service.api.domain.SendRequest;
import com.daken.message.service.api.domain.SendResponse;
import com.daken.message.service.api.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
public class SendController {

    @Autowired
    private SendService sendService;

    @PostMapping
    public BasicResultVO<String> send(@RequestBody SendRequest sendRequest){
        SendResponse sendResponse = sendService.send(sendRequest);
        return new BasicResultVO<>(sendResponse.getCode(), sendResponse.getMsg());
    }

}
