package com.github.surpassm.iot.modbus.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *
 * @author ares
 */
public class ModbusHeader {
    /**
     * 报文头->事务标识符 Transaction Identifier
     */
    private final int transactionIdentifier;
    /**
     * 报文头->协议标识符 Protocol Identifier
     */
    private final int protocolIdentifier;
    /**
     * 报文头->长度
     */
    private final int length;
    /**
     * 报文头->单位符
     */
    private final short unitIdentifier;

    public ModbusHeader(int transactionIdentifier, int protocolIdentifier, int pduLength, short unitIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
        this.protocolIdentifier = protocolIdentifier;
        //+ unit identifier
        this.length = pduLength + 1;
        this.unitIdentifier = unitIdentifier;
    }

    public int getLength() {
        return length;
    }

    public int getProtocolIdentifier() {
        return protocolIdentifier;
    }

    public int getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public short getUnitIdentifier() {
        return unitIdentifier;
    }

    public static ModbusHeader decode(ByteBuf buffer) {
        return new ModbusHeader(buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedByte());
    }

    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer();

        buf.writeShort(transactionIdentifier);
        buf.writeShort(protocolIdentifier);
        buf.writeShort(length);
        buf.writeByte(unitIdentifier);

        return buf;
    }

    @Override
    public String toString() {
        return "ModbusHeader{" + "transactionIdentifier=" + transactionIdentifier + ", protocolIdentifier=" + protocolIdentifier + ", length=" + length + ", unitIdentifier=" + unitIdentifier + '}';
    }
}
