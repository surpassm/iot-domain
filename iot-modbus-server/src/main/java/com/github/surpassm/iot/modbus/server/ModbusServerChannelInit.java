package com.github.surpassm.iot.modbus.server;

import com.github.surpassm.iot.modbus.config.ModbusConfig;
import com.github.surpassm.iot.modbus.constant.ModbusConstants;
import com.github.surpassm.iot.modbus.handler.ModbusDecoder;
import com.github.surpassm.iot.modbus.handler.ModbusEncoder;
import com.github.surpassm.iot.modbus.handler.ModbusRequestHandler;
import com.github.surpassm.iot.modbus.handler.ModbusResponseHandler;
import com.github.surpassm.iot.modbus.pojo.ModbusFrame;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import javax.annotation.Resource;

/**
 * @author mc
 * Create date 2019/11/10 10:08
 * Version 1.0
 * Description ModbusChannelInitializer
 */
public class ModbusServerChannelInit extends ChannelInitializer<SocketChannel> {

    @Resource
    private ModbusConfig.ModbusServerConfig serverConfig;

    private final SimpleChannelInboundHandler handler;

    public ModbusServerChannelInit(ModbusRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        /*
         * Modbus-TCP帧描述
         *  - max. 260 Byte (ADU = 7 Byte MBAP + 253 Byte PDU)
         *  - 长度字段包括单元标识符+PDU
         * <----------------------------------------------- ADU -------------------------------------------------------->
         * <---------------------------- BAOWE -----------------------------------------><------------- PDU ------------>
         * +------------------------+---------------------+----------+-----------------++---------------+---------------+
         * | Transaction Identifier | Protocol Identifier | Length   | Unit Identifier || Function Code | Data          |
         * | (2 Byte)               | (2 Byte)            | (2 Byte) | (1 Byte)        || (1 Byte)      | (1 - 252 Byte |
         * +------------------------+---------------------+----------+-----------------++---------------+---------------+
         */
        pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(ModbusConstants.MAX_FRAME_LENGTH, ModbusConstants.LENGTH_FIELD_OFFSET, ModbusConstants.LENGTH_FIELD_LENGTH))
                //Modbus encoder, decoder
                .addLast("encoder", new ModbusEncoder())
                .addLast("decoder", new ModbusDecoder(handler instanceof ModbusRequestHandler));

        if (handler instanceof ModbusRequestHandler) {
            //server
            pipeline.addLast("requestHandler", handler);
        } else if (handler instanceof ModbusResponseHandler) {
            //async client
            pipeline.addLast("responseHandler", handler);
        } else {
            //sync client
            pipeline.addLast("responseHandler", new ModbusResponseHandler() {
                @Override
                public void newResponse(ModbusFrame frame) {
                    //discard in sync mode
                }
            });
        }


    }
}
