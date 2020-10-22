package com.github.surpassm.iot.modbus.controller;

import com.github.surpassm.iot.modbus.pojo.ModbusFrame;
import com.github.surpassm.iot.modbus.pojo.ModbusHeader;
import com.github.surpassm.iot.modbus.pojo.SessionStore;
import com.github.surpassm.iot.modbus.service.SessionStoreService;
import io.netty.buffer.ByteBuf;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author mc
 * Create date 2020/10/22 12:12
 * Version 1.0
 * Description
 */

@CrossOrigin
@RestController
@RequestMapping("/test/")
public class TestController {

    @Resource
    private SessionStoreService sessionStoreService;

    @PostMapping("all")
    public Object all() {
        return sessionStoreService.findAll();
    }
    @PostMapping("send")
    public void send(String clientId) {
        byte[] bytes = {(byte) 0xf8, 0x04, 0x00, 0x00, 0x00, 0x0a, 0x64, 0x64};
        SessionStore sessionStore = sessionStoreService.get(clientId);
        sessionStore.getChannel().writeAndFlush(bytes);

    }


}
