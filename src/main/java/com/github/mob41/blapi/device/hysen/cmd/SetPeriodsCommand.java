package com.github.mob41.blapi.device.hysen.cmd;

import com.github.mob41.blapi.device.hysen.Period;

public class SetPeriodsCommand extends BaseHysenCommand {

    private final byte[] cmd = new byte[] { 0x01, 0x10, 0x00, 0x0a, 0x00, 0x0c, 0x18, //
            0x0, 0x0, 0x0, // wd p1
            0x0, 0x0, 0x0, // wd p2
            0x0, 0x0, 0x0, // wd p3
            0x0, 0x0, 0x0, // wd p4
            0x0, 0x0, 0x0, // wd p5
            0x0, 0x0, 0x0, // wd p6

            0x0, 0x0, 0x0, // we p1
            0x0, 0x0, 0x0, // we p1
            0x0, 0x0, 0x0, // we p1
            0x0, 0x0, 0x0, // we p1
    };

    public SetPeriodsCommand(Period[] schedule) {
        super();
        assert schedule != null;
        assert schedule.length == 8;

        int offset = 7;
        for (Period s : schedule) {
            cmd[offset++] = (byte) (s.getStartHour() & 0xFF);
            cmd[offset++] = (byte) (s.getStartMinute() & 0xFF);
        }

        for (Period s : schedule) {
            cmd[offset++] = (byte) ((int) (s.getTemp() * 2) & 0xFF);
        }
    }

    @Override
    protected byte[] getCmdBytes() {
        return cmd;
    }

}
