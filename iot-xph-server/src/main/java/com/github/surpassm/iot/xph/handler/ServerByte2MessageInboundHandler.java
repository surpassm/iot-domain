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
        log.info("新增一个客户端连接：" + ctx.channel().id().asShortText());
        //todo 待写业务逻辑
        SessionStore sessionStore = new SessionStore(ctx.channel().id().asShortText(), ctx.channel(), false);
        sessionStoreService.put(ctx.channel().id().asShortText(), sessionStore);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("移除一个客户端连接：" + ctx.channel().id().asShortText());
        //todo 待写业务逻辑
        sessionStoreService.remove(ctx.channel().id().asShortText());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bt1 = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bt1);
        super.channelRead(ctx, msg);
    }
}
