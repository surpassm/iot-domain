package com.github.surpassm.iot.modbus.constant;

/**
 * @author mc
 * Create date 2020/5/20 15:03
 * Version 1.0
 * Description
 */
public class ModbusConstants {
    public static final int ERROR_OFFSET = 0x80;
    /**
     * milliseconds
     */
    public static final int SYNC_RESPONSE_TIMEOUT = 2000;
    /**
     * affects memory usage of library
     */
    public static final int TRANSACTION_IDENTIFIER_MAX = 100;

    /**
     * LengthFieldBasedFrameDecoder : maxFrameLength 发送的数据帧最大长度
     */
    public static final int MAX_FRAME_LENGTH = 260;
    /**
     * LengthFieldBasedFrameDecoder : lengthFieldOffset
     * 定义长度域位于发送的字节数组中的下标。换句话说：发送的字节数组中下标为${lengthFieldOffset}的地方是长度域的开始地方
     */
    public static final int LENGTH_FIELD_OFFSET = 4;
    /**
     * LengthFieldBasedFrameDecoder : lengthFieldLength
     * 用于描述定义的长度域的长度。换句话说：发送字节数组bytes时, 字节数组bytes[lengthFieldOffset, lengthFieldOffset+lengthFieldLength]
     * 域对应于的定义长度域部分
     */
    public static final int LENGTH_FIELD_LENGTH = 2;
    /**
     *
     */
    public static final int MBAP_LENGTH = 7;

    public static final int DEFAULT_MODBUS_PORT = 502;
    public static final short DEFAULT_PROTOCOL_IDENTIFIER = 0;
    public static final short DEFAULT_UNIT_IDENTIFIER = 255;
}
