package com.daken.handler.handler;

import org.springframework.stereotype.Component;


import java.util.HashMap;

@Component
public class HandlerHolder {
    private HashMap<Integer, Handler> map = new HashMap<>();

    public void putHandler(Integer channelCode, Handler handler){
        map.put(channelCode, handler);
    }
    public Handler getHandler(Integer channelCode){
        return map.get(channelCode);
    }
}
