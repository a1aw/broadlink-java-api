package com.github.mob41.blapi;

public final class ByteToHexString {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    private ByteToHexString() {
    }

    /**
     * Converts the given byte array to a hex string where each byte is represented by exactly two characters.
     * For example, a byte
     *
     * @param data the array to convert
     * @return the resulting hex string
     */
    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
}
