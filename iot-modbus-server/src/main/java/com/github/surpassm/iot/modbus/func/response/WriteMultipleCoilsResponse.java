package com.github.surpassm.iot.modbus.func.response;


import com.github.surpassm.iot.modbus.func.AbstractFunction;

public class WriteMultipleCoilsResponse extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfOutputs = 1 - 2000 (0x07D0)
    public WriteMultipleCoilsResponse() {
        super(WRITE_MULTIPLE_COILS);
    }

    public WriteMultipleCoilsResponse(int startingAddress, int quantityOfOutputs) {
        super(WRITE_MULTIPLE_COILS, startingAddress, quantityOfOutputs);
    }

    public int getStartingAddress() {
        return address;
    }

    public int getQuantityOfOutputs() {
        return value;
    }

    @Override
    public String toString() {
        return "WriteMultipleCoilsResponse{" + "startingAddress=" + address + ", quantityOfOutputs=" + value + '}';
    }
}
