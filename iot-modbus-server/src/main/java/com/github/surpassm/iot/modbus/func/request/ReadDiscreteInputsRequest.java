package com.github.surpassm.iot.modbus.func.request;


import com.github.surpassm.iot.modbus.func.AbstractFunction;

public class ReadDiscreteInputsRequest extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfCoils = 1 - 2000 (0x07D0)
    public ReadDiscreteInputsRequest() {
        super(READ_DISCRETE_INPUTS);
    }

    public ReadDiscreteInputsRequest(int startingAddress, int quantityOfCoils) {
        super(READ_DISCRETE_INPUTS, startingAddress, quantityOfCoils);
    }

    public int getStartingAddress() {
        return address;
    }

    public int getQuantityOfCoils() {
        return value;
    }

    @Override
    public String toString() {
        return "ReadDiscreteInputsRequest{" + "startingAddress=" + address + ", quantityOfCoils=" + value + '}';
    }
}
