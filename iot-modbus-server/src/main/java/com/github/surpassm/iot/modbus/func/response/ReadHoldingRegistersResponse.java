package com.github.surpassm.iot.modbus.func.response;

import com.github.surpassm.iot.modbus.pojo.ModbusFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ReadHoldingRegistersResponse extends ModbusFunction {

    private short byteCount;
    private int[] registers;

    public ReadHoldingRegistersResponse() {
        super(READ_HOLDING_REGISTERS);
    }

    public ReadHoldingRegistersResponse(int[] registers) {
        super(READ_HOLDING_REGISTERS);

        // maximum of 125 registers
        if (registers.length > 125) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) (registers.length * 2);
        this.registers = registers;
    }

    public int[] getRegisters() {
        return registers;
    }

    public short getByteCount() {
        return byteCount;
    }

    @Override
    public int calculateLength() {
        return 1 + 1 + byteCount;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeByte(byteCount);

        for (int i = 0; i < registers.length; i++) {
            buf.writeShort(registers[i]);
        }

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        byteCount = data.readUnsignedByte();

        registers = new int[byteCount / 2];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = data.readUnsignedShort();
        }
    }

    @Override
    public String toString() {
        StringBuilder registersStr = new StringBuilder();
        registersStr.append("{");
        for (int i = 0; i < registers.length; i++) {
            registersStr.append("register_");
            registersStr.append(i);
            registersStr.append("=");
            registersStr.append(registers[i]);
            registersStr.append(", ");
        }
        registersStr.delete(registersStr.length() - 2, registersStr.length());
        registersStr.append("}");

        return "ReadHoldingRegistersResponse{" + "byteCount=" + byteCount + ", inputRegisters=" + registersStr + '}';
    }
}
