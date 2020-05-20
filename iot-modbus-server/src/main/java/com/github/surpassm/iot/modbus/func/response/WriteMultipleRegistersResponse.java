package com.github.surpassm.iot.modbus.func.response;


import com.github.surpassm.iot.modbus.func.AbstractFunction;

public class WriteMultipleRegistersResponse extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfRegisters = 1 - 123 (0x07D0)
    public WriteMultipleRegistersResponse() {
        super(WRITE_MULTIPLE_REGISTERS);
    }

    public WriteMultipleRegistersResponse(int startingAddress, int quantityOfRegisters) {
        super(WRITE_MULTIPLE_REGISTERS, startingAddress, quantityOfRegisters);
    }

    public int getStartingAddress() {
        return address;
    }

    public int getQuantityOfRegisters() {
        return value;
    }

    @Override
    public String toString() {
        return "WriteMultipleRegistersResponse{" + "startingAddress=" + address + ", quantityOfRegisters=" + value + '}';
    }
}
