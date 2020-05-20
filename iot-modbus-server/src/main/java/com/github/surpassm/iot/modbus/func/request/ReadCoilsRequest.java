package com.github.surpassm.iot.modbus.func.request;


import com.github.surpassm.iot.modbus.func.AbstractFunction;


public class ReadCoilsRequest extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfCoils = 1 - 2000 (0x07D0)
    public ReadCoilsRequest() {
        super(READ_COILS);
    }

    public ReadCoilsRequest(int startingAddress, int quantityOfCoils) {
        super(READ_COILS, startingAddress, quantityOfCoils);
    }

    public int getStartingAddress() {
        return address;
    }

    public int getQuantityOfCoils() {
        return value;
    }

    @Override
    public String toString() {
        return "ReadCoilsRequest{" + "startingAddress=" + address + ", quantityOfCoils=" + value + '}';
    }
}
