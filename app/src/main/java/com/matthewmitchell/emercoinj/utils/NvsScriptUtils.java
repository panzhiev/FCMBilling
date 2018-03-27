package com.matthewmitchell.emercoinj.utils;


import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class NvsScriptUtils {

    private static final int OP_NAME_NEW = 1;
    private static final int OP_NAME_DELETE = 3;
    private final long SATOSHI = 1000000;
    private final long DEFAULT_FEE = 100;
    private static final int VALUE_CHUNK_LENGTH = 520;

    public double getNvsFee(String name, String value, String days, int op) {

        double newFee = (double) DEFAULT_FEE / SATOSHI;

        if (!(op == OP_NAME_DELETE)) {

            boolean isNewNvs = op == OP_NAME_NEW;

            int nameBytesLength = name.getBytes().length;
            int valueBytesLength = value.getBytes().length;
            double number = 5020;
            double difficulty = 2495191283786.39990234;
            double baseFee = isNewNvs ? DEFAULT_FEE / SATOSHI : 0.0;

            double s = nameBytesLength + valueBytesLength;
            double n = isNewNvs ? 1.0 : 0.0;
            double y = Double.parseDouble(days) / 365;
            double r = number / Math.sqrt(Math.sqrt(difficulty));

            newFee = Math.ceil(Math.sqrt(r * (n + y)) + Math.floor(s / 128) + baseFee) / 10000;

            if (newFee == 0){
                newFee = (double) DEFAULT_FEE / SATOSHI;
            }
        }

        return newFee;
    }

    public static byte[] getBytesFromDays(String days) {

        byte[] daysBytes;

        daysBytes = ByteBuffer
                .allocate(Integer.SIZE / 8)
                .putInt(Integer.parseInt(days))
                .array();

        ArrayUtils.reverse(daysBytes);

        return daysBytes;
    }

    public static byte[] removeZeroBytes(byte[] valueChunk) {
        return new String(valueChunk).replaceAll("\0", "").getBytes();
    }

    public static byte[][] divideValue(byte[] source) {

        byte[][] ret = new byte
                [(int) Math.ceil(source.length / (double) VALUE_CHUNK_LENGTH)]
                [VALUE_CHUNK_LENGTH];
        int start = 0;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source, start, start + VALUE_CHUNK_LENGTH);
            start += VALUE_CHUNK_LENGTH;
        }
        return ret;
    }
}
