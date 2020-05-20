package com.github.surpassm.iot.modbus.func.request;


import com.github.surpassm.iot.modbus.func.AbstractFunction;

public class ReadInputRegistersRequest extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfInputRegisters = 1 - 125
    public ReadInputRegistersRequest() {
        super(READ_INPUT_REGISTERS);
    }

    public ReadInputRegistersRequest(int startingAddress, int quantityOfInputRegisters) {
        super(READ_INPUT_REGISTERS, startingAddress, quantityOfInputRegisters);
    }

    public int getStartingAddress() {
        return address;
    }

    public int getQuantityOfInputRegisters() {
        return value;
    }

    @Override
    public String toString() {
        return "ReadInputRegistersRequest{" + "startingAddress=" + address + ", quantityOfInputRegisters=" + value + '}';
    }
}
