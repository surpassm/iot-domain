package com.github.surpassm.iot.xph.controller;

import cn.hutool.core.convert.Convert;
import com.github.surpassm.iot.xph.pojo.SessionStore;
import com.github.surpassm.iot.xph.service.SessionStoreService;
import com.github.surpassm.iot.xph.util.Crc16;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
    public void send(String clientId, byte[] bytes) {
        SessionStore sessionStore = sessionStoreService.get(clientId);
        Channel channel = sessionStore.getChannel();
        byte[] data = ArrayUtils.addAll(bytes, Crc16.crc16(bytes));
        channel.writeAndFlush(Unpooled.copiedBuffer(data)).addListener(i -> {
            if (i.isSuccess()) {
                log.info("发送成功");
            } else {
                log.info("发送失败");
            }
        });

    }



}
