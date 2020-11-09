package com.github.surpassm.iot.xph.util;

/**
 * @author mc
 * Create date 2020/11/9 15:22
 * Version 1.0
 * Description
 */
public class Crc16 {
    /**
     * 将int转换成byte数组，低位在前，高位在后
     * 改变高低位顺序只需调换数组序号
     */
    private static byte[] intToBytes(int value)  {
        byte[] src = new byte[2];
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    public static byte[] crc16(byte[] buf) {
        int len = buf.length;
        int crc = 0xFFFF;
        int polynomial = 0xA001;
        if (len == 0) { return new byte[0]; }
        for (byte b : buf) {
            crc ^= ((int) b & 0x00FF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= polynomial;
                } else {
                    crc >>= 1;
                }
            }
        }
        return intToBytes(crc);
    }

}
