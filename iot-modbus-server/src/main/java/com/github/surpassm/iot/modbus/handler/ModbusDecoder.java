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
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author mc
 * Create date 2020/5/21 9:43
 * Version 1.0
 * Description 解码核心处理器
 */
public class ModbusDecoder extends ByteToMessageDecoder {
    private Logger log = LoggerFactory.getLogger(getClass());
    private final boolean serverMode;

    public ModbusDecoder(boolean serverMode) {
        this.serverMode = serverMode;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        /*Function Code*/
        if (buffer.capacity() < ModbusConstants.MBAP_LENGTH + 1) {
            log.info("buffer 容量小于:{}",(ModbusConstants.MBAP_LENGTH + 1));
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


    protected static short[] convertToShorts(byte[] data) {
        short[] sdata = new short[data.length / 2];
        for (int i = 1; i < sdata.length; i++) {
            int i1 = i * 3;
            if (i == 4){
                sdata[i - 1] = toShort(data[13], data[14]);
                continue;
            }
            if (i == 5){
                sdata[i - 1] = toShort(data[17], data[18]);
                continue;
            }
            if (i == 6){
                sdata[i - 1] = toShort(data[i1 + 1], data[i1 + 2]);
                break;
            }

            sdata[i - 1] = toShort(data[i1], data[i1 + 1]);
        }
        return sdata;
    }

    public static short toShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xff));
    }


    /**
     * 字节转十六进制
     * @param b 需要进行转换的byte字节
     * @return  转换后的Hex字符串
     */
    public static String byteToHex(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if(hex.length() < 2){
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 16进制转10进制
     *
     * @param hex
     * @return
     */
    public static int hex2decimal(String hex) {
        return Integer.parseInt(hex, 16);
    }
}
