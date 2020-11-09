package com.github.surpassm.iot.xph.server;

import com.github.surpassm.iot.xph.config.XphConfig;
import com.github.surpassm.iot.xph.handler.MsgDecoderHandler;
import com.github.surpassm.iot.xph.handler.MsgEncoderHandler;
import com.github.surpassm.iot.xph.handler.ServerByte2MessageInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author mc
 * Create date 2019/11/10 10:08
 * Version 1.0
 * Description ModbusChannelInitializer
 */
public class XphServerChannelInit extends ChannelInitializer<SocketChannel> {

    private XphConfig.ModbusServerConfig serverConfig;

    private ServerByte2MessageInboundHandler handler;

    public XphServerChannelInit(ServerByte2MessageInboundHandler handler ,XphConfig.ModbusServerConfig serverConfig){
        this.handler = handler;
        this.serverConfig = serverConfig;
    }


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(handler)
        ;


    }
}
