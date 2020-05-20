package com.github.surpassm.iot.modbus.func.response;

import com.github.surpassm.iot.modbus.pojo.ModbusFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.BitSet;

public class ReadCoilsResponse extends ModbusFunction {

    private short byteCount;
    private BitSet coilStatus;

    public ReadCoilsResponse() {
        super(READ_COILS);
    }

    public ReadCoilsResponse(BitSet coilStatus) {
        super(READ_COILS);

        byte[] coils = coilStatus.toByteArray();

        // maximum of 2000 bits
        if (coils.length > 250) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) coils.length;
        this.coilStatus = coilStatus;
    }

    public BitSet getCoilStatus() {
        return coilStatus;
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
        buf.writeBytes(coilStatus.toByteArray());

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        byteCount = data.readUnsignedByte();

        byte[] coils = new byte[byteCount];
        data.readBytes(coils);

        coilStatus = BitSet.valueOf(coils);
    }

    @Override
    public String toString() {
        return "ReadCoilsResponse{" + "byteCount=" + byteCount + ", coilStatus=" + coilStatus + '}';
    }
}
