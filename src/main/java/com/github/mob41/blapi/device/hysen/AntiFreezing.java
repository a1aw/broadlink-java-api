package com.github.mob41.blapi.device.hysen;

/**
 * Anti-freezing function
 * 
 * 00:anti-freezing function shut down
 * 
 * 01:anti-freezing function open
 * 
 * 00:anti-freezing function shut down
 * 
 */
public enum AntiFreezing {
    SHUT_DOWN((byte) 0x00), //
    OPEN((byte) 0x01);

    private final byte value;

    private AntiFreezing(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static AntiFreezing fromValue(byte val) {
        switch (val) {
        case 0x00:
            return SHUT_DOWN;
        case 0x01:
            return OPEN;
        default:
            return SHUT_DOWN;
        }
    }
}
