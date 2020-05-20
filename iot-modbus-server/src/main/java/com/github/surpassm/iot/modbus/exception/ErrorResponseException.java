package com.github.surpassm.iot.modbus.exception;


import com.github.surpassm.iot.modbus.func.ModbusError;

public class ErrorResponseException extends Exception {

    int exceptionCode;

    public ErrorResponseException(ModbusError function) {
        super(function.toString());
    }
}
