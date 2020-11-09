package com.github.surpassm.iot.modbus.controller;

import com.github.surpassm.iot.modbus.func.ModbusError;
import com.github.surpassm.iot.modbus.pojo.ModbusFrame;
import com.github.surpassm.iot.modbus.pojo.ModbusFunction;
import com.github.surpassm.iot.modbus.pojo.ModbusHeader;
import com.github.surpassm.iot.modbus.pojo.SessionStore;
import com.github.surpassm.iot.modbus.service.SessionStoreService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.nio.ByteBuffer;

/**
 * @author mc
 * Create date 2020/10/22 12:12
 * Version 1.0
 * Description
 */
@Slf4j
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
    public void send(String clientId,byte[] bytes) {
        SessionStore sessionStore = sessionStoreService.get(clientId);
        sessionStore.getChannel().writeAndFlush(Unpooled.copiedBuffer(bytes)).addListener(i -> {
            if (i.isSuccess()) {
                log.info("发送成功");
            }else {
                log.info("发送失败");
            }
        });

    }


}
