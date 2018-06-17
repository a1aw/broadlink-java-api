package com.github.mob41.blapi.dev.hysen;

/**
 * Power on memory
 * 
 * 00:Power on no need memory
 * 
 * 01:Power on need memory
 * 
 * default: 00:Power on no need memory
 * 
 */
public enum PowerOnMemory {
    ON_NO_NEED_MEM((byte) 0), //
    ON_NEED_MEM((byte) 1);

    private final byte value;

    private PowerOnMemory(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static PowerOnMemory fromValue(byte val) {
        switch (val) {
        case 0:
            return ON_NO_NEED_MEM;
        case 1:
            return ON_NEED_MEM;
        default:
            return ON_NO_NEED_MEM;
        }
    }
}
