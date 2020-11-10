package com.github.surpassm.iot.xph.handler;

import com.github.surpassm.iot.xph.config.ThreadConfig;
import com.github.surpassm.iot.xph.pojo.SessionStore;
import com.github.surpassm.iot.xph.service.SessionStoreService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mc
 * Create date 2020/11/9 12:53
 * Version 1.0
 * Description
 */
@Getter
@Setter
@Slf4j
@ChannelHandler.Sharable
public class ServerByte2MessageInboundHandler extends ChannelInboundHandlerAdapter {

    private ThreadConfig threadConfig;
    private SessionStoreService sessionStoreService;

    public void setServer(ThreadConfig threadConfig, SessionStoreService sessionStoreService) {
        this.sessionStoreService = sessionStoreService;
        this.threadConfig = threadConfig;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelShortId = ctx.channel().id().asShortText();
        String socketAddress = ctx.channel().remoteAddress().toString();
        String s = socketAddress.split(":")[0];
        String address = s.substring(1);
        log.info("新增客户端连接：{},{}", channelShortId, address);
        //todo 待写业务逻辑
        SessionStore sessionStore = new SessionStore(channelShortId, ctx.channel(), false);
        sessionStoreService.put(channelShortId, sessionStore);
        log.info("当前连接数：{}", sessionStoreService.size());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelShortId = ctx.channel().id().asShortText();
        log.info("移除一个客户端连接：{}", channelShortId);
        //todo 待写业务逻辑
        if (sessionStoreService.containsKey(channelShortId)) {
            sessionStoreService.remove(channelShortId);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bt1 = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bt1);
        StringBuilder builder = new StringBuilder();
        for (byte b : bt1) {
            builder.append(b).append(" ");
        }
        log.info("客户端返回：{}", builder.toString());
        super.channelRead(ctx, msg);
    }
}
