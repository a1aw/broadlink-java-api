package com.github.mob41.blapi;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ByteToHexStringTest {
    @Test
    public void toHexBinary() {
        byte[] source = new byte[Byte.MAX_VALUE - Byte.MIN_VALUE + 1];
        for (int i = 0; i <= Byte.MAX_VALUE - Byte.MIN_VALUE; i++) {
            //noinspection NumericCastThatLosesPrecision
            source[i] = (byte) (i + Byte.MIN_VALUE);
        }
        StringBuilder sb = new StringBuilder((Byte.MAX_VALUE - Byte.MIN_VALUE + 1) * 2);
        for (byte b : source) {
            sb.append(String.format("%02X", b));
        }
        String expectedResult = sb.toString();

        assertThat(ByteToHexString.toHexString(source), is(expectedResult));
    }
}