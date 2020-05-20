package com.github.surpassm.iot.modbus.handler;

import com.github.surpassm.iot.modbus.constant.ModbusConstants;
import com.github.surpassm.iot.modbus.func.ModbusError;
import com.github.surpassm.iot.modbus.func.WriteSingleCoil;
import com.github.surpassm.iot.modbus.func.WriteSingleRegister;
import com.github.surpassm.iot.modbus.func.request.*;
import com.github.surpassm.iot.modbus.func.response.*;
import com.github.surpassm.iot.modbus.pojo.ModbusFrame;
import com.github.surpassm.iot.modbus.pojo.ModbusFunction;
import com.github.surpassm.iot.modbus.pojo.ModbusHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class ModbusDecoder extends ByteToMessageDecoder {

    private final boolean serverMode;

    public ModbusDecoder(boolean serverMode) {
        this.serverMode = serverMode;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        /*Function Code*/
        if (buffer.capacity() < ModbusConstants.MBAP_LENGTH + 1) {
            return;
        }

        ModbusHeader mbapHeader = ModbusHeader.decode(buffer);

        short functionCode = buffer.readUnsignedByte();

        ModbusFunction function = null;
        switch (functionCode) {
            case ModbusFunction.READ_COILS:
                if (serverMode) {
                    function = new ReadCoilsRequest();
                } else {
                    function = new ReadCoilsResponse();
                }
                break;
            case ModbusFunction.READ_DISCRETE_INPUTS:
                if (serverMode) {
                    function = new ReadDiscreteInputsRequest();
                } else {
                    function = new ReadDiscreteInputsResponse();
                }
                break;
            case ModbusFunction.READ_INPUT_REGISTERS:
                if (serverMode) {
                    function = new ReadInputRegistersRequest();
                } else {
                    function = new ReadInputRegistersResponse();
                }
                break;
            case ModbusFunction.READ_HOLDING_REGISTERS:
                if (serverMode) {
                    function = new ReadHoldingRegistersRequest();
                } else {
                    function = new ReadHoldingRegistersResponse();
                }
                break;
            case ModbusFunction.WRITE_SINGLE_COIL:
                function = new WriteSingleCoil();
                break;
            case ModbusFunction.WRITE_SINGLE_REGISTER:
                function = new WriteSingleRegister();
                break;
            case ModbusFunction.WRITE_MULTIPLE_COILS:
                if (serverMode) {
                    function = new WriteMultipleCoilsRequest();
                } else {
                    function = new WriteMultipleCoilsResponse();
                }
                break;
            case ModbusFunction.WRITE_MULTIPLE_REGISTERS:
                if (serverMode) {
                    function = new WriteMultipleRegistersRequest();
                } else {
                    function = new WriteMultipleRegistersResponse();
                }
                break;
            default:
                break;
        }

        if (ModbusFunction.isError(functionCode)) {
            function = new ModbusError(functionCode);
        } else if (function == null) {
            function = new ModbusError(functionCode, (short) 1);
        }

        function.decode(buffer.readBytes(buffer.readableBytes()));

        ModbusFrame frame = new ModbusFrame(mbapHeader, function);

        out.add(frame);
    }
}
