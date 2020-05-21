package com.github.surpassm.iot.modbus.handler;

import com.github.surpassm.iot.modbus.config.ThreadConfig;
import com.github.surpassm.iot.modbus.func.WriteSingleCoil;
import com.github.surpassm.iot.modbus.func.WriteSingleRegister;
import com.github.surpassm.iot.modbus.func.request.*;
import com.github.surpassm.iot.modbus.func.response.*;
import com.github.surpassm.iot.modbus.pojo.ModbusFrame;
import com.github.surpassm.iot.modbus.pojo.ModbusFunction;
import com.github.surpassm.iot.modbus.pojo.ModbusHeader;
import com.github.surpassm.iot.modbus.server.ModbusServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ModbusRequestHandler extends SimpleChannelInboundHandler<ModbusFrame> {

    private static final Logger logger = Logger.getLogger(ModbusRequestHandler.class.getSimpleName());
    private ModbusServer server;
    private ThreadConfig threadConfig;

    public void setServer(ModbusServer server, ThreadConfig threadConfig) {
        this.server = server;
        this.threadConfig = threadConfig;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning(cause.getLocalizedMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.removeClient(ctx.channel());
    }

    private static byte[] bytes = {(byte) 0xf8, 0x04, 0x00, 0x00, 0x00, 0x0a, 0x64, 0x64};

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        server.addClient(ctx.channel());
//        threadConfig.getAsyncExecutor().execute(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(1000 * 3);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer(bytes)).addListener(i -> {
//                        if (!i.isSuccess()) {
//                            ctx.channel().close();
//                            ctx.close();
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    ctx.channel().close();
//                    ctx.close();
//                    break;
//                }
//            }
//        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusFrame frame) throws Exception {
        Channel channel = ctx.channel();

        ModbusFunction function = frame.getFunction();

        ModbusFunction response;

        logger.log(Level.FINER, function.toString());

        if (function instanceof WriteSingleCoil) {
            WriteSingleCoil request = (WriteSingleCoil) function;

            response = writeSingleCoil(request);
        } else if (function instanceof WriteSingleRegister) {
            WriteSingleRegister request = (WriteSingleRegister) function;

            response = writeSingleRegister(request);
        } else if (function instanceof ReadCoilsRequest) {
            ReadCoilsRequest request = (ReadCoilsRequest) function;

            response = readCoilsRequest(request);
        } else if (function instanceof ReadDiscreteInputsRequest) {
            ReadDiscreteInputsRequest request = (ReadDiscreteInputsRequest) function;

            response = readDiscreteInputsRequest(request);
        } else if (function instanceof ReadInputRegistersRequest) {
            ReadInputRegistersRequest request = (ReadInputRegistersRequest) function;

            response = readInputRegistersRequest(request);
        } else if (function instanceof ReadHoldingRegistersRequest) {
            ReadHoldingRegistersRequest request = (ReadHoldingRegistersRequest) function;

            response = readHoldingRegistersRequest(request);
        } else if (function instanceof WriteMultipleRegistersRequest) {
            WriteMultipleRegistersRequest request = (WriteMultipleRegistersRequest) function;

            response = writeMultipleRegistersRequest(request);
        } else if (function instanceof WriteMultipleCoilsRequest) {
            WriteMultipleCoilsRequest request = (WriteMultipleCoilsRequest) function;

            response = writeMultipleCoilsRequest(request);
        } else {
            throw new UnsupportedOperationException("Function not supported!");
        }

        ModbusHeader header = new ModbusHeader(
                frame.getHeader().getTransactionIdentifier(),
                frame.getHeader().getProtocolIdentifier(),
                response.calculateLength(),
                frame.getHeader().getUnitIdentifier());

        ModbusFrame responseFrame = new ModbusFrame(header, response);

        channel.write(responseFrame);
    }

    protected abstract WriteSingleCoil writeSingleCoil(WriteSingleCoil request);

    protected abstract WriteSingleRegister writeSingleRegister(WriteSingleRegister request);

    protected abstract ReadCoilsResponse readCoilsRequest(ReadCoilsRequest request);

    protected abstract ReadDiscreteInputsResponse readDiscreteInputsRequest(ReadDiscreteInputsRequest request);

    protected abstract ReadInputRegistersResponse readInputRegistersRequest(ReadInputRegistersRequest request);

    protected abstract ReadHoldingRegistersResponse readHoldingRegistersRequest(ReadHoldingRegistersRequest request);

    protected abstract WriteMultipleRegistersResponse writeMultipleRegistersRequest(WriteMultipleRegistersRequest request);

    protected abstract WriteMultipleCoilsResponse writeMultipleCoilsRequest(WriteMultipleCoilsRequest request);
}
