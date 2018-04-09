package com.github.mob41.blapi.device.hysen.cmd;

public class SetModeCommand extends GenericByteCommand {

    public SetModeCommand(byte loop_mode, byte sensor, byte osv, byte dif, byte svh, byte svl, double adj, byte fre,
            byte poweron) {
        super(new byte[] { 0x01, 0x10, 0x00, 0x02, 0x00, 0x05, 0x0a, loop_mode, sensor, osv, dif, svh, svl,
                (byte) ((int) (adj * 2) >> 8 & 0xff), (byte) ((int) (adj * 2) & 0xff), fre, poweron });
    }

    public SetModeCommand(byte auto_mode, byte loop_mode, byte sensor) {
        super(new byte[] { 0x01, 0x06, 0x00, 0x02, ((byte) (((loop_mode + 1) << 4) + auto_mode)), sensor });
    }
}
