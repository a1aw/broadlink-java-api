package com.github.mob41.blapi.dev.hysen;

/**
 * loop_mode refers to index in [ "12345,67", "123456,7", "1234567" ] E.g.
 * loop_mode = 0 ("12345,67") means Saturday and Sunday follow the "weekend"
 * schedule loop_mode = 2 ("1234567") means every day (including Saturday and
 * Sunday) follows the "weekday" schedule
 * 
 * @author alpapad
 *
 */
public enum LoopMode {
    D12345_67(0x00), //
    D123456_7(0x01), //
    D1234567(0x02);

    private final byte value;

    private LoopMode(int value) {
        this.value = (byte) (value & 0xFF);
    }

    public byte getValue() {
        return value;
    }

    public static LoopMode fromValue(byte val) {
        switch (val) {
        case 0x00:
            return D12345_67;
        case 0x01:
            return D123456_7;
        case 0x02:
            return D1234567;
        default:
            return D12345_67;
        }
    }
}
