package com.github.surpassm.iot.xph.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author mc
 * Create date 2020/11/9 14:05
 * Version 1.0
 * Description
 */
@Slf4j
public class MsgDecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bt1 = new byte[byteBuf.readableBytes()];
        log.info(bt1.toString());
    }

}
