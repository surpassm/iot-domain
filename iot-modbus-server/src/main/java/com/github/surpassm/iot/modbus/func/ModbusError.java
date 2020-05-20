package com.github.surpassm.iot.modbus.func;

import com.github.surpassm.iot.modbus.pojo.ModbusFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;

public class ModbusError extends ModbusFunction {
    /*
     * Modbus异常代码
     *
     * 01非法函数
     * 查询中接收到的函数代码不是服务器（或从机）。这可能是因为函数代码适用于较新的设备，但未在单元中实现
     * 选择。它还可以指示服务器（或从机）位于处理此类请求的状态错误，例如未配置，正在要求返回寄存器值。
     *
     * 02非法数据地址
     * 查询中接收的数据地址不是服务器（或从机）。更具体地说，参照系的组合数字和传输长度无效。对于100的控制器
     * 寄存器，PDU将第一个寄存器寻址为0，最后一个寄存器寻址为99年。如果请求是以起始注册地址96和寄存器数量为4，则此请求将成功运行
     * （至少按地址）在寄存器96、97、98、99上。如果请求是提交的起始注册地址为96，数量为寄存器为5，则此请求将失败，异常代码为0x02
     * “非法数据地址”，因为它试图在寄存器96、97上操作，98、99和100，没有地址为100的寄存器。
     *
     * 03非法数据值
     * 查询数据字段中包含的值不是服务器（或从机）。这表明复杂请求的剩余部分，例如隐含长度为不正确。它并不意味着寄存器中的存储值超出了
     * 应用程序，因为MODBUS协议不知道任何特定登记册的任何特定价值的重要性。
     *
     * 04从设备故障
     * 当服务器（或从属服务器）处于正在尝试执行请求的操作。
     *
     * 05确认
     * 与编程命令结合使用。服务器（或slave）已接受请求并正在处理，但持续时间很长这样做需要时间。返回此响应是为了防止在客户端（或主机）
     * 中发生超时错误。客户（或master）可以下一次发出轮询程序完成消息以确定处理完成。
     *
     * 06从设备忙
     * 与编程命令结合使用。服务器（或slave）正在处理一个长时间的程序命令。这个客户端（或主服务器）应在服务器稍后重新传输消息时（或奴隶）
     * 是自由的。
     *
     * 08存储器奇偶校验错误
     * 与功能代码20和21以及引用类型6，指示扩展文件区域未能通过一致性检查。服务器（或从机）试图读取记录文件，但在内存中检测到奇偶校验错
     * 误。客户（或主人）可以重试请求，但服务器（或从机）上可能需要服务装置。
     *
     *0A网关路径不可用
     * 与网关一起专用，表示网关无法从输入端口分配内部通信路径到用于处理请求的输出端口。通常意味着网关配置错误或过载。
     *
     *0B网关目标设备未能响应
     * 专门用于网关，表示没有响应从目标设备获取。通常意味着设备不是出现在网络上。
     */

    private static final HashMap<Short, String> ERRORS = new HashMap<>();

    static {
        ERRORS.put((short) (0x01), "ILLEGAL FUNCTION");
        ERRORS.put((short) (0x02), "ILLEGAL DATA ADDRESS");
        ERRORS.put((short) (0x03), "ILLEGAL DATA VALUE");
        ERRORS.put((short) (0x04), "SLAVE DEVICE FAILURE");
        ERRORS.put((short) (0x05), "ACKNOWLEDGE");
        ERRORS.put((short) (0x06), "SLAVE DEVICE BUSY");
        ERRORS.put((short) (0x08), "MEMORY PARITY ERROR");
        ERRORS.put((short) (0x0A), "GATEWAY PATH UNAVAILABLE");
        ERRORS.put((short) (0x0B), "GATEWAY TARGET DEVICE FAILED TO RESPOND");
    }

    private short exceptionCode;
    private String exceptionMessage;

    public ModbusError(short functionCode) {
        super(functionCode);
    }

    public ModbusError(short functionCode, short exceptionCode) {
        super(functionCode);
        this.exceptionCode = exceptionCode;

    }

    private void setExceptionMessage(short exceptionCode) {
        this.exceptionMessage = ERRORS.get(exceptionCode) != null ? ERRORS.get(exceptionCode) : "UNDEFINED ERROR";
    }

    public short getExceptionCode() {
        return exceptionCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    public int calculateLength() {
        return 1 + 1;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeByte(exceptionCode);

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        exceptionCode = data.readUnsignedByte();

        setExceptionMessage(exceptionCode);
    }

    @Override
    public String toString() {
        return "ModbusError{" + "exceptionCode=" + exceptionCode + ", exceptionMessage=" + exceptionMessage + '}';
    }
}
