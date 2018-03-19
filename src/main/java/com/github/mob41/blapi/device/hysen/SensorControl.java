package com.github.mob41.blapi.device.hysen;

/**
 * Sensor control option
 * 
 * 0:internal sensor
 * 
 * 1:external sensor
 * 
 * 2:internal control temperature,external limit temperature
 * 
 * default: 0:internal sensor
 * 
 * @return
 */
public enum SensorControl {

    INTERNAL((byte) 0x00), //
    EXTERNAL((byte) 0x01), //
    INTERNAL_TEMP_EXTERNAL_LIMIT((byte) 0x02);

    private final byte value;

    private SensorControl(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static SensorControl fromValue(byte val) {
        switch (val) {
        case 0x00:
            return INTERNAL;
        case 0x01:
            return EXTERNAL;
        case 0x02:
            return INTERNAL_TEMP_EXTERNAL_LIMIT;
        default:
            return INTERNAL;
        }
    }
}
